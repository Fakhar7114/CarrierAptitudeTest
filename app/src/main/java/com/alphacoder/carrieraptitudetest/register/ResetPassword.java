package com.alphacoder.carrieraptitudetest.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ActivityResetPasswordBinding;
import com.alphacoder.carrieraptitudetest.helpers.EditTextValueChecker;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ResetPassword extends AppCompatActivity {
    String TAG="ResetPassword";
    ActivityResetPasswordBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize objects
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        executor= Executors.newSingleThreadExecutor();

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(ResetPassword.this)){

                    if (EditTextValueChecker.hasValue(binding.etEmail)){
                        String email=binding.etEmail.getText().toString().trim();
                        resetPassword(email);
                    }
                }
                else {
                    UiHelper.toast(ResetPassword.this,"No Internet Connection");
                }
            }
        });

    }

    private void resetPassword(String email) {

        executor.submit(new Runnable() {
            @Override
            public void run() {

                try {

                        auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("QueryPermissionsNeeded")
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful() && task.isComplete()){

                                            binding.etEmail.setText("");
                                            UiHelper.toast(ResetPassword.this,"Password reset link sent to your email");
                                            finish();


                                        }
                                        else{
                                            UiHelper.toast(ResetPassword.this,"Something went wrong");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG,"Error: "+e.getMessage());
                                    }
                                });

                }
                catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                }


            }
        });

    }
}