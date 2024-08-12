package com.alphacoder.carrieraptitudetest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacoder.carrieraptitudetest.activities.ViewDetail;
import com.alphacoder.carrieraptitudetest.constant.ResultStatus;
import com.alphacoder.carrieraptitudetest.databinding.ItemFieldsBinding;
import com.alphacoder.carrieraptitudetest.models.Category;
import com.alphacoder.carrieraptitudetest.models.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG="ResultAdapter";
    Context context;
    List<Result> results;
    List<Result> filteredList;
    ResultCallback callback;
    Map<String, Category> categoriesMap;




    public ResultAdapter(Context context, List<Result> result,ResultCallback callback) {
        this.context = context;
        this.results = result;
        filteredList=new ArrayList<>(result);
        this.callback=callback;
        categoriesMap=new HashMap<>();
        applyFilter();


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFieldsBinding binding=ItemFieldsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ResultHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ResultHolder){
            ((ResultHolder) holder).bind(context,filteredList.get(position),callback,categoriesMap);
        }
    }

    @Override
    public int getItemCount() {

        return filteredList.size();
    }

    private void applyFilter(){
        filteredList.clear();
        for (Result result:results){
            int percentage=calculatePercentage(result.getTotalQuestions(),result.getQuestionCount(),result.getCorrectAns());
            Log.d(TAG,"Percentage"+percentage+"\nCorrect Answers "+result.getCorrectAns());
            if (percentage>60){
                filteredList.add(result);
            }
        }
        callback.onSize(filteredList.size());
    }


    public static class ResultHolder extends RecyclerView.ViewHolder {

        static String TAG="ResultHolder";
        ItemFieldsBinding  binding;

        public ResultHolder(@NonNull ItemFieldsBinding itemView) {
            super(itemView.getRoot());
            this.binding=itemView;
        }
        @SuppressLint("SetTextI18n")
        public void bind(Context context, Result result, ResultCallback callback, Map<String, Category> categoriesMap){


            Log.d(TAG,"Correct Answers :"+result.getCorrectAns());

            Category category=new Category(result.getName(),result.getIcon());
            // Total Percentage for this Field/Category
            double percentage=calculatePercentage(result.getTotalQuestions(),result.getQuestionCount(), result.getCorrectAns());
            category.setPercentage(percentage);

            categoriesMap.put(category.getName(),category);
            if (percentage>=90){
                callback.onResult(categoriesMap, ResultStatus.EXCELLENT);

            } else if (percentage>=80) {
                callback.onResult(categoriesMap, ResultStatus.GOOD);

            } else if (percentage>=65) {
                callback.onResult(categoriesMap, ResultStatus.SATISFY);
            }
            else {
                callback.onResult(categoriesMap,ResultStatus.BAD);
            }

            binding.tvFieldName.setText(result.getName());
            binding.imgField.setImageResource(result.getIcon());
            binding.tvQuestionCount.setText(result.getQuestionCount()+"/"+result.getTotalQuestions());



            binding.bntDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewDetail.name=result.getName();
                    context.startActivity(new Intent(context, ViewDetail.class));
                }
            });

        }

    }
    private static int calculatePercentage(int totalQues, int attemptedQues, int correctAns){

        int percentage=0;
        if (attemptedQues==0 || correctAns==0 ){
            return percentage;
        }
        else{

            percentage = (int) (((double) correctAns / attemptedQues) * 100);

        }

        Log.d("ResultHolder","TotalQues :"+totalQues+"\nAttempted Ques :"+attemptedQues+"\nPercentage :"+percentage+" %");

        return percentage;

    }
    @SuppressLint("NotifyDataSetChanged")
    public void setResults(List<Result> list){
        results.addAll(list);
        notifyDataSetChanged();
    }

    public interface ResultCallback{
        void onResult(Map<String,Category> categoryMap,String status);
        void onSize(int size);
    }


}
