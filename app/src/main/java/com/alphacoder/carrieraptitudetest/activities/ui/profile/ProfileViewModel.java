package com.alphacoder.carrieraptitudetest.activities.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends ViewModel {

    String TAG="ProfileViewModel";
    ExecutorService executor;
    MutableLiveData<User> user;

    public ProfileViewModel() {
        executor= Executors.newSingleThreadExecutor();
        user=new MutableLiveData<>();
    }

    public void getUser(){

        executor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    if (DataHolder.user!=null){

                        user.postValue(DataHolder.user);
                    }
                    else{
                        user.postValue(null);
                    }

                }
                catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                }

            }
        });



    }

}