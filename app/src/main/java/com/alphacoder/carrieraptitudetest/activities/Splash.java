package com.alphacoder.carrieraptitudetest.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ActivitySplashBinding;
import com.alphacoder.carrieraptitudetest.helpers.AuthHelper;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.register.Login;


public class Splash extends AppCompatActivity {

    AuthHelper authHelper;
    Animation fadeAnim;
    ActivitySplashBinding binding;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        authHelper=new AuthHelper();

        // changing status bar  and navigation bar color
        getWindow().setStatusBarColor(getColor(R.color.purple_500));
        getWindow().setNavigationBarColor(getColor(R.color.purple_500));

        setContentView(binding.getRoot());


        fadeAnim= AnimationUtils.loadAnimation(this, R.anim.anim_text_fade_in);
        binding.textView5.setAnimation(fadeAnim);
        sharedPref=new SharedPref(this);



        // moving to next Welcome Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

              if (!sharedPref.getBoolean(SharedPref.PREF_SETTING,"is_first_time")){
                  startActivity(new Intent(Splash.this, Onboarding.class));
                  sharedPref.setBoolean(SharedPref.PREF_SETTING,"is_first_time",true);
              }
              else {
                  if (authHelper.isLoggedIn() &&authHelper.isVerified()){
                      startActivity(new Intent(Splash.this, MainActivity.class));
                  }
                  else {
                      startActivity(new Intent(Splash.this, Login.class));
                  }

              }
                finish();


            }
        }, 2200);

    }
}