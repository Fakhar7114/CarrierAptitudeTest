package com.alphacoder.carrieraptitudetest.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.adapters.QuestionAdapter;
import com.alphacoder.carrieraptitudetest.adapters.ResultAdapter;
import com.alphacoder.carrieraptitudetest.adapters.diffUtil.QuestionDiffUtil;
import com.alphacoder.carrieraptitudetest.constant.CategoryConstant;
import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.databinding.ActivityResultScreenBinding;
import com.alphacoder.carrieraptitudetest.databinding.DialogTestSubmittedBinding;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.Category;
import com.alphacoder.carrieraptitudetest.models.Questions;
import com.alphacoder.carrieraptitudetest.models.Result;
import com.alphacoder.carrieraptitudetest.models.ResultDetail;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class ResultScreen extends AppCompatActivity implements QuestionAdapter.QuestionCallback, ResultAdapter.ResultCallback {

    String TAG = "ResultScreenLog";
    ActivityResultScreenBinding binding;
    // RecyclerView Object
    QuestionAdapter questionAdapter;
    public static Map<Integer, Questions> questionsMap;
    ResultAdapter resultAdapter;
    List<Result> resultList;
    public static Map<String, Integer> totalQuestions;
    public static int totalQues;
    public static int attemptedQues;
    public static boolean isCategory;
    FirebaseDatabase database;
    ExecutorService executor;
    String uid = "";
    User user;
    public static String category = "";
    String status;
    double percentage;
    boolean isAdded = false;
    Map<String, Category> categoryMapString;
    public static Map<String, Integer> correctAnswers;
    Loading loading;
    String KEY_IS_FIRST_TIME="is_first_time";
    boolean isFirstTime=true;
    String FAILURE="Your Result is too low to be submitted\nPlease try again to do your best";


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityResultScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Initializing objects
        resultList = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
        database = FirebaseDatabase.getInstance();
        user = DataHolder.user;
        uid = user.getId();
        categoryMapString = new HashMap<>();


        if (bundle!=null){
            isFirstTime=bundle.getBoolean(KEY_IS_FIRST_TIME);
        }



        // recyclerview setup
        recyclerViewSetup();

        // Result recyclerview setup
        resultRecyclerViewSetup();





        Log.d(TAG, "User :" + user + "\n id" + uid);


        // Adding result to Database with Firebase
        binding.btnSaveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(ResultScreen.this)) {
                    if (!isAdded) {
                        addResult();
                    }

                } else {
                    UiHelper.toast(ResultScreen.this, "No Internet Connection");
                }

            }
        });


    }

    private void addResult() {

        loading=new Loading(this);

        String id = UUID.randomUUID().toString();
        long timeStamp = System.currentTimeMillis();


        ResultDetail detail = new ResultDetail();
        detail.setStudentId(uid);
        detail.setTestDate(timeStamp);
        detail.setTestName(category);
        detail.setTotalQuestions(totalQues);
        detail.setAttemptedQuestions(attemptedQues);
        detail.setId(id);
        detail.setStudentName(user.getName());
        detail.setResultStatus(status);
        detail.setCategoryMap(categoryMapString);

        executor.submit(() -> {
            try {
                database.getReference(FirebaseConstant.RESULT)
                        .child(user.getId())
                        .child(id)
                        .setValue(detail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loading.dismiss();
                                Log.d(TAG, "Successfully Result added");
                                isAdded = true;
                                binding.btnSaveResult.setEnabled(false);
                                dialogTestSubmitted();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to add result");
                                UiHelper.toast(ResultScreen.this, "Failed to add result");
                                loading.dismiss();
                            }
                        });


            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                loading.dismiss();
            }

        });
    }

    private void recyclerViewSetup() {
        List<Questions> questionsList = new ArrayList<>(getQuestions());
        questionAdapter = new QuestionAdapter(this, false, true, new QuestionDiffUtil(), this);
        questionAdapter.submitList(questionsList);
        binding.rvQuestions.setAdapter(questionAdapter);
    }

    private void resultRecyclerViewSetup() {
        resultList.clear();
        resultList = getResult();
        resultAdapter = new ResultAdapter(this, resultList, this);
        binding.rvFields.setAdapter(resultAdapter);

        Log.d(TAG, "Result List size :" + resultList.size());


    }

    private List<Questions> getQuestions() {
        List<Questions> list = new ArrayList<>();
        for (Questions questions : questionsMap.values()) {
            list.add(questions);
        }
        return list;
    }

    private List<Result> getResult() {

        Map<String, Integer> categoryCountMap = new HashMap<>();
        // Aggregate counts per category
        for (Questions question : questionsMap.values()) {
            if (question.getSelectedOption() != -1) { // Check if the question was attempted
                String category = question.getCategory();
                int count = categoryCountMap.getOrDefault(category, 0);
                categoryCountMap.put(category, count + 1);
            }
        }

        // Create Result objects based on the aggregated counts
        List<Result> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue();
            int totalQuestion = getTotalQuestion(category);
            int correctAnswer=getCorrectAnswers(category);


            switch (category) {
                case CategoryConstant.ARTS:
                    list.add(new Result(CategoryConstant.ARTS, count, totalQuestion, R.drawable.ic_arts,correctAnswer));
                    break;
                case CategoryConstant.BOTANY:

                    list.add(new Result(CategoryConstant.BOTANY, count, totalQuestion, R.drawable.ic_botany,correctAnswer));
                    break;
                case CategoryConstant.CS:
                    list.add(new Result(CategoryConstant.CS, count, totalQuestion, R.drawable.ic_cs,correctAnswer));
                    break;
                case CategoryConstant.ENGLISH:
                    list.add(new Result(CategoryConstant.ENGLISH, count, totalQuestion, R.drawable.ic_english,correctAnswer));
                    break;
                case CategoryConstant.HISTORY:
                    list.add(new Result(CategoryConstant.HISTORY, count, totalQuestion, R.drawable.ic_history,correctAnswer));
                    break;
                case CategoryConstant.CHEMISTRY:
                    list.add(new Result(CategoryConstant.CHEMISTRY, count, totalQuestion, R.drawable.ic_chemistry,correctAnswer));
                    break;
                case CategoryConstant.PHYSICS:
                    list.add(new Result(CategoryConstant.PHYSICS, count, totalQuestion, R.drawable.ic_physics,correctAnswer));
                    break;
                case CategoryConstant.MATH:
                    list.add(new Result(CategoryConstant.MATH, count, totalQuestion, R.drawable.ic_math,correctAnswer));
                    break;
                case CategoryConstant.GENERAL_KNOWLEDGE:
                    list.add(new Result(CategoryConstant.GENERAL_KNOWLEDGE, count, totalQuestion, R.drawable.ic_world,correctAnswer));
                    break;
                case CategoryConstant.ISLAMIAT:
                    list.add(new Result(CategoryConstant.ISLAMIAT, count, totalQuestion, R.drawable.ic_islamiate,correctAnswer));
                    break;
                case CategoryConstant.URDU:
                    list.add(new Result(CategoryConstant.URDU, count, totalQuestion, R.drawable.ic_urdu,correctAnswer));
                    break;
                case CategoryConstant.PHYSICAL_SCIENCE:
                    list.add(new Result(CategoryConstant.PHYSICAL_SCIENCE, count, totalQuestion, R.drawable.ic_physical_science,correctAnswer));
                    break;
                case CategoryConstant.LITERATURE:
                    list.add(new Result(CategoryConstant.LITERATURE, count, totalQuestion, R.drawable.ic_literature,correctAnswer));
                    break;
                case CategoryConstant.ZOOLOGY:
                    list.add(new Result(CategoryConstant.ZOOLOGY, count, totalQuestion, R.drawable.ic_zoology,correctAnswer));
                    break;
            }
        }

        return list;
    }

    private int getTotalQuestion(String category) {
        int count = 0;
        if (totalQuestions.containsKey(category)) {
            count = totalQuestions.get(category);
        }
        return count;
    }
    private int getCorrectAnswers(String category) {
        int count = 0;
        if (correctAnswers.containsKey(category)) {
            count = correctAnswers.get(category);
        }
        return count;
    }


    private void dialogTestSubmitted() {
        DialogTestSubmittedBinding binding1 = DialogTestSubmittedBinding.inflate(LayoutInflater.from(ResultScreen.this), null, false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(binding1.getRoot());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        binding1.btnGoToHome.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(ResultScreen.this, MainActivity.class));
        });
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(this.getColorStateList(R.color.white));
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(shapeDrawable);
        dialog.show();


    }

    private void dialogBadTest() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Don't Be Discouraged!");
        builder.setCancelable(false);
        builder.setMessage("It looks like this test was a bit challenging. That's okay! Everyone has areas to improve. Review your mistakes, learn from them, and try again. You’re capable of achieving great results with a bit more practice!");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ;
            }
        });
        AlertDialog dialog = builder.create();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(this.getColorStateList(R.color.white));
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(shapeDrawable);
        dialog.show();


    }


    @Override
    public void onAttempt(Map<Integer, Questions> mapQuestions, Map<String, Integer> correctAnswers) {


        Log.d(TAG,"Correct Answers :"+correctAnswers.size());


    }


    @Override
    public void onResult(Map<String, Category> categoryMap, String status) {
        this.status = status;
        this.categoryMapString = categoryMap;

        categoryMap.forEach((string, category) -> {
            try {
                Log.d(TAG, "Percentage: " + category.getPercentage());
                Log.d(TAG, "Category: " + category.toString());
            } catch (Exception e) {
                Log.e(TAG, "Exception occurred while logging: " + e.getMessage());
            }
        });





    }

    @Override
    public void onSize(int size) {
        if (size == 0) {
            binding.btnSaveResult.setVisibility(View.GONE);
            if (isFirstTime){
                dialogBadTest();
            }

            binding.tv.setText(FAILURE);

        }
        Log.d(TAG,"Size :"+size);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_FIRST_TIME,false);

    }
}