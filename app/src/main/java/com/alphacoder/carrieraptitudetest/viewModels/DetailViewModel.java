package com.alphacoder.carrieraptitudetest.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.models.Detail;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailViewModel extends ViewModel {

    String TAG="DetailViewModel";
    ExecutorService executor = Executors.newSingleThreadExecutor();;
    FirebaseDatabase  database= FirebaseDatabase.getInstance();
    public MutableLiveData<Detail> detail=new MutableLiveData<>();




    public void getDetail(String name){
        Log.d(TAG,"Key "+name);

        executor.submit(()->{
            try {
               Query query= database.getReference(FirebaseConstant.SCOPE)
                        .child(FirebaseConstant.SCOPE);

               query.orderByChild("name")
                        .equalTo(name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Log.d(TAG,"Data Snapshot: "+snapshot);
                                if (snapshot.exists()){

                                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                        String name = childSnapshot.child("name").getValue(String.class);
                                        String description = childSnapshot.child("description").getValue(String.class);

                                        List<String> carriers =new ArrayList<>();
                                        DataSnapshot carrierSnap=childSnapshot.child("careers");
                                        for (DataSnapshot snap:carrierSnap.getChildren()){
                                            String career=snap.getValue(String.class);
                                            carriers.add(career);

                                        }

                                        Detail detail = new Detail();
                                        detail.setName(name);
                                        detail.setDescription(description);
                                        detail.setCareers(carriers);

                                        DetailViewModel.this.detail.postValue(detail);
                                        Log.d(TAG,"Data name : "+name);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "getDetail Error: "+error.toString());
                            }
                        });
            }
            catch (Exception e){
                Log.e(TAG, "getDetail Exception: "+e.getMessage());
            }
        });
    }
}
