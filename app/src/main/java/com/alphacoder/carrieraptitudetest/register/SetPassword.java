package com.alphacoder.carrieraptitudetest.register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.databinding.ActivitySetPasswordBinding;
import com.alphacoder.carrieraptitudetest.helpers.EditTextValueChecker;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.helpers.StorageHelper;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SetPassword extends AppCompatActivity {

    String TAG = "SetPasswordLog";
    ActivitySetPasswordBinding binding;
    public static User user;
    public static Uri uri;
    FirebaseDatabase database;
    Loading loading;
    StorageHelper storageHelper;
    FirebaseUser firebaseUser;
    ExecutorService executor;
    FirebaseAuth auth;
    SharedPref sharedPref;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing objects
        database = FirebaseDatabase.getInstance();
        executor= Executors.newSingleThreadExecutor();;
        auth = FirebaseAuth.getInstance();
        sharedPref=new SharedPref(this);
        gson=new Gson();


        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkConnection.isAvailable(SetPassword.this)) {

                    if (EditTextValueChecker.hasValue(binding.etPassword)
                            && EditTextValueChecker.hasValue(binding.etConfirmPass)) {

                        String password = binding.etPassword.getText().toString().trim();
                        String confirmPass = binding.etConfirmPass.getText().toString().trim();

                        if (password.equals(confirmPass)) {

                            loading = new Loading(SetPassword.this);

                            if (uri != null) {

                                profileSetup(confirmPass);
                            } else {
                                signUpUser(confirmPass);
                            }

                        } else {
                            UiHelper.toast(SetPassword.this, "Both password must be same ");
                        }

                    }

                } else {
                    UiHelper.toast(SetPassword.this, "No Internet Connection");
                }

            }
        });
    }

    private void profileSetup(String password) {


        storageHelper = new StorageHelper(this);
        storageHelper.uploadFile(uri, new StorageHelper.OnFileUploadedListener() {
            @Override
            public void onUploadSuccess(String url) {

                user.setImage(url);
                signUpUser(password);

            }

            @Override
            public void onUploadFailure(Exception e) {
                loading.dismiss();
                Log.e(TAG, "Exception" + e.getMessage());
            }

            @Override
            public void onUploadProgress(int progress) {
                Log.d(TAG, "Progress " + progress + "%");
            }
        });

    }


    private void signUpUser(String password) {

        String email = user.getEmail();
        executor.submit(() -> {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful()) {
                        firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful() && task.isComplete()) {
                                                UiHelper.toast(SetPassword.this,"Verification Email has been Sent");
                                                storeUserInfo();
                                            }
                                        }
                                    });
                        } else {

                            Log.e(TAG, "user is null");
                        }


                    }
                }).addOnFailureListener(e -> {
                });

            } catch (Exception e) {

                Log.e(TAG, "Exception" + e.getMessage());
            }

        });
    }


    private void storeUserInfo() {

        String uId = auth.getUid();
        user.setId(uId);

        database.getReference(FirebaseConstant.USER)
                .child(uId)
                .setValue(user)
                .addOnSuccessListener(unused -> {

                    binding.etPassword.setText("");
                    binding.etConfirmPass.setText("");

                    Log.d(TAG, "User Info Stored" +user);

                    // Storing user info in SharedPreference
                    savePreference();

                    loading.dismiss();

                    Intent i = new Intent(SetPassword.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }).addOnFailureListener(e -> {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                });
    }


    private void savePreference(){
        sharedPref.putString(SharedPref.PREF_USER,"user",gson.toJson(user));
        Log.d(TAG,"User data stored in Shared Pref ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user = null;

    }
}