package com.alphacoder.carrieraptitudetest.activities.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.adapters.CategoryAdapter;
import com.alphacoder.carrieraptitudetest.constant.CategoryConstant;
import com.alphacoder.carrieraptitudetest.models.Category;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    Context context;

    List<Category> categories;
    CategoryAdapter categoryAdapter;


    public HomeViewModel(Context context) {
        this.context=context.getApplicationContext();
        categories=new ArrayList<>();
        categories=getCategories();


    }
    public List<Category> getCategories(){
        categories.clear();
        categories.add(new Category(CategoryConstant.CS, R.drawable.ic_cs));
        categories.add(new Category(CategoryConstant.MATH, R.drawable.ic_math));
        categories.add(new Category(CategoryConstant.PHYSICS, R.drawable.ic_physics));
        categories.add(new Category(CategoryConstant.BOTANY, R.drawable.ic_botany));
        categories.add(new Category(CategoryConstant.ZOOLOGY, R.drawable.ic_zoology));
        categories.add(new Category(CategoryConstant.URDU, R.drawable.ic_urdu));
        categories.add(new Category(CategoryConstant.ISLAMIAT, R.drawable.ic_islamiate));
        categories.add(new Category(CategoryConstant.ENGLISH, R.drawable.ic_english));
        categories.add(new Category(CategoryConstant.LITERATURE, R.drawable.ic_literature));
        categories.add(new Category(CategoryConstant.GENERAL_KNOWLEDGE, R.drawable.ic_world));
        categories.add(new Category(CategoryConstant.HISTORY, R.drawable.ic_history));
        categories.add(new Category(CategoryConstant.CHEMISTRY, R.drawable.ic_chemistry));
        categories.add(new Category(CategoryConstant.PHYSICAL_SCIENCE, R.drawable.ic_physical_science));
        categories.add(new Category(CategoryConstant.ARTS, R.drawable.ic_arts));
        return categories;
    }

    public CategoryAdapter adapter(){
        if (categoryAdapter==null){
            categoryAdapter=new CategoryAdapter(context, categories);
        }
        return categoryAdapter;
    }

}