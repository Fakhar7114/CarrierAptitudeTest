package com.alphacoder.carrieraptitudetest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alphacoder.carrieraptitudetest.activities.CategoryQuiz;
import com.alphacoder.carrieraptitudetest.databinding.ItemCategoryBinding;
import com.alphacoder.carrieraptitudetest.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Category> categories;



    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding=ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryHolder){
            ((CategoryHolder) holder).bind(context,categories.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public static class CategoryHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;
        public CategoryHolder(@NonNull ItemCategoryBinding itemView) {
            super(itemView.getRoot());
            this.binding=itemView;
        }
        public void bind(Context context,Category category){

            binding.tvCategoryName.setText(category.getName());
            binding.imgCategory.setImageResource(category.getIcon());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CategoryQuiz.categoryName=category.getName();
                    Intent intent=new Intent(context,CategoryQuiz.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setCategories(List<Category> list){
        this.categories.clear();
        this.categories.addAll(list);
        notifyDataSetChanged();
    }


}
