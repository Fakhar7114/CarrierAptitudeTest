package com.alphacoder.carrieraptitudetest.activities.ui.quiz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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

    String TAG="QuizViewModel";

    public MutableLiveData<List<Questions>> question=new MutableLiveData<>();
    ExecutorService executor= Executors.newSingleThreadExecutor();
     List<Questions> currentQuestions = new ArrayList<>();

    int PAGE_SIZE=20;
    FirebaseDatabase database;

    public QuizViewModel() {

        database=FirebaseDatabase.getInstance();

    }

   public void getQuestions(String lastKey){

       Query query=database.getReference(FirebaseConstant.QUESTIONS);

       executor.submit(new Runnable() {
           @Override
           public void run() {

               try {
                   if (lastKey==null){

                       query.orderByKey()
                               .limitToFirst(PAGE_SIZE)
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                                       List<Questions> list=new ArrayList<>();

                                       for (DataSnapshot snap:snapshot.getChildren()){
                                           Questions questions=snap.getValue(Questions.class);
                                           if (questions!=null){
                                               list.add(questions);
                                           }
                                       }

                                   if (!list.isEmpty()){

                                       if (lastKey==null){

                                           currentQuestions.addAll(list);
                                           question.postValue(currentQuestions);

                                       }
                                       else {
                                           list.remove(0);
                                           currentQuestions.addAll(list);
                                           question.postValue(currentQuestions);

                                       }
                                   }



                                       Log.d(TAG,"starting pagination "+snapshot.getChildren());
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                       Log.e(TAG,"Error "+error.toString());
                                   }
                               });

                   }
                   else {
                       query.orderByKey()
                               .startAt(lastKey)
                               .limitToFirst(PAGE_SIZE)
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                                       List<Questions> list=new ArrayList<>();

                                       for (DataSnapshot snap:snapshot.getChildren()){
                                           Questions questions=snap.getValue(Questions.class);
                                           if (questions!=null){
                                               list.add(questions);
                                           }
                                       }

                                       if (!list.isEmpty()){
                                           question.postValue(list);
                                       }

                                       Log.d(TAG,"pagination "+list.size());
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       Log.e(TAG,"Error "+error.toString());
                                   }
                               });
                   }
               }
               catch (Exception e){
                   Log.e(TAG,"Exception "+e.getMessage());
               }
           }
       });

   }
}