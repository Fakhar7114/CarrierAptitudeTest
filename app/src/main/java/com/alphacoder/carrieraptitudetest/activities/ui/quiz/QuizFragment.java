package com.alphacoder.carrieraptitudetest.activities.ui.quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacoder.carrieraptitudetest.activities.CategoryQuiz;
import com.alphacoder.carrieraptitudetest.activities.ResultScreen;
import com.alphacoder.carrieraptitudetest.adapters.QuestionAdapter;
import com.alphacoder.carrieraptitudetest.adapters.diffUtil.QuestionDiffUtil;
import com.alphacoder.carrieraptitudetest.databinding.FragmentQuizBinding;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.Questions;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment implements QuestionAdapter.QuestionCallback {

    String TAG = "QuizFragment";

    private FragmentQuizBinding binding;
    QuizViewModel quizViewModel;

    // RecyclerView Object
    QuestionAdapter questionAdapter;
    List<Questions> listQuestions;
    Map<Integer, Questions> questions;
    int totalQuestions = 0;
    int attemptedQuestions = 0;
    Map<String,Integer> mapTotalQuestions;
    Loading loading;
    Map<String, Integer> correctAnswers;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        binding = FragmentQuizBinding.inflate(inflater, container, false);


        // Initializing objects
        mapTotalQuestions=new HashMap<>();
        loading=new Loading(requireContext());
        correctAnswers=new HashMap<>();

        // Setup RecyclerView
        recyclerViewSetup();

        // Loading Question form Firebase Db with pagination
        loadQuestions();











        // Navigating to result screen
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the map is properly initialized
                if (questions == null || questions.isEmpty()) {
                    UiHelper.toast(requireContext(), "No questions attempted. Please attempt at least 10 questions.");
                    return;
                }

                if (mapTotalQuestions == null || mapTotalQuestions.isEmpty()) {
                    return;
                }

                // Check if the user has attempted enough questions
                if (attemptedQuestions >=10) {
                    // Pass data to ResultScreen
                    ResultScreen.questionsMap = questions;
                    ResultScreen.totalQuestions = mapTotalQuestions;
                    ResultScreen.attemptedQues=attemptedQuestions;
                    ResultScreen.totalQues=totalQuestions;
                    ResultScreen.isCategory=false;
                    ResultScreen.category="All";
                    ResultScreen.correctAnswers=correctAnswers;
                    startActivity(new Intent(requireContext(), ResultScreen.class));


                } else {
                    UiHelper.toast(requireContext(), "Please attempt at least 10 questions.");
                }

                Log.d(TAG, "Total questions: " + mapTotalQuestions.size());
            }
        });


        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(requireContext())){
                    loading=new Loading(requireContext());
                    loadQuestions();

                }
                else{
                    UiHelper.toast(requireContext(),"No Internet Connection");
                }
            }
        });


        binding.rvQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.rvQuestions.getLayoutManager();
                if (linearLayoutManager != null) {
                    int totalItem = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (totalItem <= lastVisibleItem+5) {
                        quizViewModel.getQuestions(String.valueOf(totalItem));
                    }
                }
            }
        });


        return binding.getRoot();
    }

    private void recyclerViewSetup() {
        listQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(requireContext(), false, false, new QuestionDiffUtil(), this);
        binding.rvQuestions.setAdapter(questionAdapter);
    }

    private void loadQuestions() {

        if (NetworkConnection.isAvailable(requireContext())) {
            quizViewModel.getQuestions("0");
            quizViewModel.question.observe(requireActivity(), new Observer<List<Questions>>() {
                @Override
                public void onChanged(List<Questions> questions) {
                    if (!questions.isEmpty()) {

                        loading.dismiss();

                        int currentSize = listQuestions.size();
                        listQuestions.addAll(questions);
                        questionAdapter.submitList(listQuestions);
                        questionAdapter.notifyItemRangeInserted(currentSize, questions.size());
                        Log.d(TAG, "Question list size" + questions.size());
                        // Getting number of total questions
                        totalQuestions=listQuestions.size();

                        try {
                            binding.tvCount.setText(attemptedQuestions+"/"+totalQuestions+"");
                        }
                        catch (Exception ignored){

                            Log.e(TAG,"Exception attemptedQuestions/totalQuestions ");
                        }
                        // Getting number of total question
                        getTotalQuestions(listQuestions);
                        binding.btnSubmit.setEnabled(true);
                    }
                    else {

                        Log.e(TAG, "Empty question list");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                loading.dismiss();
                                binding.btnSubmit.setEnabled(false);
                                binding.btnRefresh.setVisibility(View.VISIBLE);
                            }
                        },5000);
                    }

                }
            });
        }

        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    loading.dismiss();
                    binding.btnSubmit.setEnabled(false);
                    binding.btnRefresh.setVisibility(View.VISIBLE);
                }
            },5000);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onAttempt(Map<Integer, Questions> mapQuestions, Map<String, Integer> correctAnswers) {
        this.questions = mapQuestions;
        this.attemptedQuestions=mapQuestions.size();
        this.correctAnswers=correctAnswers;
        Log.d(TAG,"Data Total Questions "+totalQuestions+"\nAttempted Questions "+attemptedQuestions);

        Log.d(TAG,"Correct Answers :"+correctAnswers.size());

        try {
            binding.tvCount.setText(attemptedQuestions+"/"+totalQuestions);
        }
        catch (Exception ignored){

            Log.e(TAG,"Exception attemptedQuestions/totalQuestions ");
        }
    }
}