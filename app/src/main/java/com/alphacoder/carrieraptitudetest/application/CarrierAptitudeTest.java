package com.alphacoder.carrieraptitudetest.application;

import android.app.Application;

import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.models.User;
import com.google.gson.Gson;

public class CarrierAptitudeTest extends Application {

    Gson gson;
    User user;
    SharedPref sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initializing objects
        gson=new Gson();
        user=new User();
        sharedPref=new SharedPref(getApplicationContext());

        getUserPreference();
    }

    private void getUserPreference(){
        String userString= sharedPref.getString(SharedPref.PREF_USER,"user");
        User user1=gson.fromJson(userString,User.class);
       if (user!=null){
           user=user1;
           DataHolder.user=user;
           DataHolder.uId=user.getId();
       }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
