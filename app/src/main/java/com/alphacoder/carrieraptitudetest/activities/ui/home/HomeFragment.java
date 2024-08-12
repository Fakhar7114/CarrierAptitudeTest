package com.alphacoder.carrieraptitudetest.activities.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alphacoder.carrieraptitudetest.activities.ViewRecord;
import com.alphacoder.carrieraptitudetest.databinding.FragmentHomeBinding;
import com.alphacoder.carrieraptitudetest.models.Category;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    String TAG="HomeFragment";
    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    public  static int id=-1;
    List<Category> backUpList;
    List<Category> filteredList;
    List<Category> backList2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new HomeViewModel(requireContext());
        binding = FragmentHomeBinding.inflate(inflater, container, false);



        // Initialize objects
        backUpList = homeViewModel.getCategories();
        if (backUpList == null) {
            backUpList = new ArrayList<>();
            Log.e(TAG, "homeViewModel.getCategories() returned null, initialized empty list.");
        } else {
            Log.d(TAG, "BackUpList size after initialization: " + backUpList.size());
        }

        filteredList = new ArrayList<>(backUpList);
        backList2=new ArrayList<>(backUpList);

        Log.d(TAG,"BackUpList size : "+backUpList.size());

        // Recycler view setup
        recyclerViewSetup();


        // Navigating to View Records Screen
        binding.btnSeeRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(requireContext(), ViewRecord.class));
            }
        });

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return binding.getRoot();
    }

    private void searchCategories(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(backList2);
            Log.d(TAG, "Query is empty. BackUpList size: " + backUpList.size());
        } else {

            for (Category category : backList2) {
                if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        Log.d(TAG, "FilteredList size after search: " + filteredList.size());
        homeViewModel.adapter().setCategories(filteredList);
    }

    private void recyclerViewSetup() {
        binding.rvCategories.setAdapter(homeViewModel.adapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}