package com.alphacoder.carrieraptitudetest.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.models.Questions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizViewModel extends ViewModel {

    String TAG = "QuizViewModel";
    public MutableLiveData<List<Questions>> questions = new MutableLiveData<>();
    ExecutorService  executor = Executors.newSingleThreadExecutor();
    FirebaseDatabase database = FirebaseDatabase.getInstance();;


    public void loadQuestions(String categoryName) {

        executor.submit(() -> {

            try {
                Query query = database.getReference(FirebaseConstant.QUESTIONS);
                query.orderByChild("category")
                        .equalTo(categoryName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                List<Questions> list = new ArrayList<>();

                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Questions questions = snap.getValue(Questions.class);
                                    if (questions != null) {
                                        list.add(questions);
                                    }
                                }
                                if (!list.isEmpty()) {
                                    questions.postValue(list);
                                }

                                Log.d(TAG, "Data " + list.size());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Log.e(TAG, "Error " + error.toString());
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.getMessage());
            }

        });


    }
}
