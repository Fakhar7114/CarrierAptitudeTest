package com.alphacoder.carrieraptitudetest.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends ViewModel {

    String TAG="UserViewModel";
    ExecutorService executor;
    FirebaseDatabase database;
    public MutableLiveData<User> user;

    public UserViewModel(){
        executor= Executors.newSingleThreadExecutor();
        database=FirebaseDatabase.getInstance();
        user=new MutableLiveData<>();
    }

    public void getData(String uId){
        Log.d(TAG,"User id "+uId);
        executor.submit(()->{
            try {
                database.getReference(FirebaseConstant.USER)
                        .child(uId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    User user1=snapshot.getValue(User.class);
                                    user.postValue(user1);

                                    Log.d(TAG,"User Data "+user1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Log.e(TAG,"Error"+error.toString());
                            }
                        });
            }
            catch (Exception e){
                Log.e(TAG,"Exception"+e.getMessage());
            }


        });
    }
}
