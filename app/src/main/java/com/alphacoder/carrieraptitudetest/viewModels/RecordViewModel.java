package com.alphacoder.carrieraptitudetest.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.models.ResultDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RecordViewModel extends ViewModel {

    String TAG="RecordViewModel";
    ExecutorService executor= Executors.newSingleThreadExecutor();;
   public MutableLiveData<List<ResultDetail>> records=new MutableLiveData<>();;
   public MutableLiveData<Boolean> isData=new MutableLiveData<>();;
   public MutableLiveData<Boolean> isLoading=new MutableLiveData<>();;
    FirebaseDatabase database=FirebaseDatabase.getInstance();;
    String uid= DataHolder.uId;
    CountDownLatch latch;
    public MutableLiveData<Boolean> isTimedOut=new MutableLiveData<>();
    int TIME_OUT=10;



    public void getRecords(){

        Log.d(TAG,"User id"+uid);
        latch=new CountDownLatch(1);

        executor.submit(()->{

            try {
                isLoading.postValue(true);
                isTimedOut.postValue(false);

               database.getReference(FirebaseConstant.RESULT)
                       .child(uid)
                       .orderByChild("testDate")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d(TAG,"Data snapshot "+snapshot.getChildren());

                        List<ResultDetail> list=new ArrayList<>();

                        for (DataSnapshot snap:snapshot.getChildren()){
                            if (snap.exists()){
                                ResultDetail resultDetail=snap.getValue(ResultDetail.class);
                                if (resultDetail!=null){
                                    list.add(resultDetail);
                                }
                            }
                        }

                        Log.d(TAG,"size of records "+list.size());
                        if (!list.isEmpty()){
                            records.postValue(list);
                            isData.postValue(true);

                        }
                        else {
                            isData.postValue(false);

                        }

                        isLoading.postValue(false);
                        latch.countDown();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"Error "+error.toString());
                        isLoading.postValue(false);
                        latch.countDown();
                    }
                });

                Log.d(TAG,"we are in try block ");

              try {
                  if (!latch.await(TIME_OUT, TimeUnit.SECONDS)){
                      isTimedOut.postValue(true);
                  }
                  else{
                      isTimedOut.postValue(false);
                  }
              }
              catch (InterruptedException e){
                  isTimedOut.postValue(true);

              }


        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            isData.postValue(false);
            isLoading.postValue(false);
        }

        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
