package com.alphacoder.carrieraptitudetest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alphacoder.carrieraptitudetest.adapters.QuestionAdapter;
import com.alphacoder.carrieraptitudetest.adapters.diffUtil.QuestionDiffUtil;
import com.alphacoder.carrieraptitudetest.databinding.ActivityCategoryQuizBinding;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.Questions;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.alphacoder.carrieraptitudetest.viewModels.QuizViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryQuiz extends AppCompatActivity implements QuestionAdapter.QuestionCallback {

    String TAG="CategoryQuizLog";
    ActivityCategoryQuizBinding binding;
    QuestionAdapter questionAdapter;
    QuizViewModel quizViewModel;
    public static String categoryName="";
    Map<Integer , Questions> questions;
    int attemptedQuestions=0;
    int totalQuestions = 0;
    Map<String,Integer> mapTotalQuestions;
    Loading loading;
    Map<String, Integer> correctAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizViewModel=new ViewModelProvider(this).get(QuizViewModel.class);
        binding=ActivityCategoryQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing objects
        binding.tvCategoryName.setText(categoryName);
        questions=new HashMap<>();
        mapTotalQuestions=new HashMap<>();
        loading=new Loading(this);
        correctAnswers=new HashMap<>();


        // setup recyclerview
        recyclerViewSetup();

        // loading questions
        loadQuestions();


        // Navigating to result screen
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (attemptedQuestions<10){
                    UiHelper.toast(CategoryQuiz.this,"Please attempt at least 10 questions");
                    return;
                }


                // Check if the user has attempted enough questions

                    // Pass data to ResultScreen
                ResultScreen.questionsMap = questions;
                ResultScreen.totalQuestions = mapTotalQuestions;
                ResultScreen.attemptedQues=attemptedQuestions;
                ResultScreen.totalQues=totalQuestions;
                ResultScreen.isCategory=true;
                ResultScreen.category=categoryName;
                ResultScreen.correctAnswers=correctAnswers;
                startActivity(new Intent(CategoryQuiz.this, ResultScreen.class));
                Log.d(TAG, "Total questions: " + mapTotalQuestions.size());
            }
        });



        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(CategoryQuiz.this)){
                    loading=new Loading(CategoryQuiz.this);
                    loadQuestions();
                }
                else {
                    UiHelper.toast(CategoryQuiz.this,"No Internet Connection");
                }

            }
        });


    }

    private void loadQuestions() {

        if (NetworkConnection.isAvailable(CategoryQuiz.this)) {
            quizViewModel.loadQuestions(categoryName);

            quizViewModel.questions.observe(this, new Observer<List<Questions>>() {
                @Override
                public void onChanged(List<Questions> questions) {
                    Log.d(TAG, "Size of questions" + questions.size());
                    if (!questions.isEmpty()) {

                        // Dismiss progress bar
                        loading.dismiss();
                        // Submitting list
                        questionAdapter.submitList(questions);
                        // getting total questions
                        totalQuestions = questions.size();

                        // Getting size of Questions
                        getTotalQuestions(questions);
                        binding.btnRefresh.setVisibility(View.GONE);
                        binding.btnSubmit.setEnabled(true);
                    }
                    else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                loading.dismiss();
                                binding.btnRefresh.setVisibility(View.VISIBLE);
                                binding.btnSubmit.setEnabled(false);

                            }
                        }, 5000);
                    }
                }

            });
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    loading.dismiss();
                    binding.btnRefresh.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setEnabled(false);

                }
            }, 5000);

        }
    }
    private void getTotalQuestions(List<Questions> questionsList) {
        mapTotalQuestions.clear();
        Log.d(TAG, "Question size for Total questions: " + questionsList.size());

        for (Questions question : questionsList) {
            String category = question.getCategory();

            if (category != null && !category.isEmpty()) {
                if (mapTotalQuestions.containsKey(category)) {
                    // If the category already exists in the map, increment its count
                    int count = mapTotalQuestions.get(category);
                    mapTotalQuestions.put(category, count + 1);
                } else {
                    // If the category does not exist in the map, add it with count 1
                    mapTotalQuestions.put(category, 1);
                }
            } else {
                Log.e(TAG, "Invalid category found in question: " + question.getCategory());
            }
        }


    }


    private void recyclerViewSetup(){
        questionAdapter=new QuestionAdapter(this,true,false,new QuestionDiffUtil(),this);
        binding.rvQuestions.setAdapter(questionAdapter);
    }

    @Override
    public void onAttempt(Map<Integer,Questions> mapQuestions, Map<String, Integer> correctAnswers) {
       this. questions = mapQuestions;
        this.attemptedQuestions=mapQuestions.size();
        this.correctAnswers=correctAnswers;
        Log.d(TAG,"Correct Answer :"+correctAnswers.size());
    }
}