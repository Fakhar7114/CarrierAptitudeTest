package com.alphacoder.carrieraptitudetest.adapters.diffUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alphacoder.carrieraptitudetest.models.Questions;

public class QuestionDiffUtil extends DiffUtil.ItemCallback<Questions> {
    @Override
    public boolean areItemsTheSame(@NonNull Questions oldItem, @NonNull Questions newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull Questions oldItem, @NonNull Questions newItem) {
        return oldItem.getQuestion().equals(newItem.getQuestion())
                && oldItem.getAnswer().equals(newItem.getAnswer())
                && oldItem.getCategory().equals(newItem.getCategory())
                && oldItem.getId()== newItem.getId()
                && oldItem.getOptions()==newItem.getOptions();
    }
}
