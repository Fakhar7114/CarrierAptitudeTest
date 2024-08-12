package com.alphacoder.carrieraptitudetest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.activities.CategoryQuiz;
import com.alphacoder.carrieraptitudetest.activities.ViewDetail;
import com.alphacoder.carrieraptitudetest.activities.ViewRecord;
import com.alphacoder.carrieraptitudetest.constant.ResultStatus;
import com.alphacoder.carrieraptitudetest.databinding.ItemCategoryBinding;
import com.alphacoder.carrieraptitudetest.databinding.ItemRecordsBinding;
import com.alphacoder.carrieraptitudetest.helpers.TimeUtil;
import com.alphacoder.carrieraptitudetest.models.Category;
import com.alphacoder.carrieraptitudetest.models.ResultDetail;

import java.util.List;
import java.util.function.BiConsumer;

public class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ResultDetail> records;


    public RecordsAdapter(Context context, List<ResultDetail> records) {
        this.context = context;
        this.records = records;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecordsBinding binding = ItemRecordsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryHolder) {
            ((CategoryHolder) holder).bind(context, records.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }


    public static class CategoryHolder extends RecyclerView.ViewHolder {
        ItemRecordsBinding binding;

        public CategoryHolder(@NonNull ItemRecordsBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Context context, ResultDetail record) {

            binding.tvStatus.setTextColor(context.getColor(R.color.light_text_color));
            String status = record.getResultStatus()!=null ?record.getResultStatus():"status";


            if (status.equalsIgnoreCase(ResultStatus.EXCELLENT)) {
                binding.tvStatus.setTextColor(context.getColor(R.color.dark_green));
            } else if (status.equalsIgnoreCase(ResultStatus.GOOD)) {
                binding.tvStatus.setTextColor(context.getColor(R.color.green));

            } else if (status.equalsIgnoreCase(ResultStatus.SATISFY)) {
                binding.tvStatus.setTextColor(context.getColor(R.color.orange));
            } else if (status.equalsIgnoreCase(ResultStatus.BAD)) {

                binding.tvStatus.setTextColor(context.getColor(R.color.red));

            }

            binding.tvTestName.setText(record.getTestName());
            binding.tvQuestionCount.setText("Result :"+record.getAttemptedQuestions() + "/" + record.getTotalQuestions());
            binding.tvStatus.setText(record.getResultStatus());
            binding.tvDateTime.setText(TimeUtil.getExactTime(record.getTestDate()));


            StringBuilder categoryString=new StringBuilder();


            record.getCategoryMap().forEach(new BiConsumer<String, Category>() {
                @Override
                public void accept(String s, Category category) {
                    categoryString.append(category.getName().toUpperCase()).append("           ").append(category.getPercentage()).append("%").append("\n");
                }
            });

            binding.tvCategoryPercentage.setText(categoryString);
            binding.tvCategoryPercentage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (record.getTestName().equals("All")){
                        return;
                    }

                    ViewDetail.name=record.getTestName();
                    Intent intent=new Intent(context, ViewDetail.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        }





    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecords(List<ResultDetail> list) {
        records.addAll(list);
        notifyDataSetChanged();
    }


}
