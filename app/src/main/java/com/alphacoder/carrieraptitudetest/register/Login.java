package com.alphacoder.carrieraptitudetest.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.alphacoder.carrieraptitudetest.activities.MainActivity;
import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.databinding.ActivityLoginBinding;
import com.alphacoder.carrieraptitudetest.helpers.AuthHelper;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.EditTextValueChecker;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.alphacoder.carrieraptitudetest.viewModels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Login extends AppCompatActivity {

    String TAG = "LoginLog";
    ActivityLoginBinding binding;
    AuthHelper authHelper;
    Loading loading;
    ExecutorService executor;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Gson gson;
    User user;
    SharedPref sharedPref;
    String uid="";
    UserViewModel userViewModel;
    FirebaseDatabase database;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth=FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        uid=auth.getUid();

        // Initializing objects
        authHelper = new AuthHelper();
        gson=new Gson();
        sharedPref=new SharedPref(this);
        userViewModel=new UserViewModel();
        executor = Executors.newSingleThreadExecutor();
        database=FirebaseDatabase.getInstance();









        // Making Sign Up text clickable
        setSignUpClickable();


        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPassword.class));

            }
        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(Login.this)) {
                    if (EditTextValueChecker.hasValue(binding.etEmail)
                            && EditTextValueChecker.hasValue(binding.etPassword)) {

                        String email = binding.etEmail.getText().toString().trim();
                        String password = binding.etPassword.getText().toString().trim();

                        loginUser(email, password);

                    }
                } else {
                    UiHelper.toast(Login.this, "No Internet Connection");
                }
            }
        });

    }


    private void setSignUpClickable() {

        String text = binding.tvDontHaveAccount.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(Login.this, SignUp.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        };
        spannableString.setSpan(span, 22, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvDontHaveAccount.setText(spannableString);
        binding.tvDontHaveAccount.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginUser(String email, String password) {

        // Loading user data from Firebase to update SharedPreference


        loading=new Loading(Login.this);

        executor.submit(() -> {
            try {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.isComplete()) {
                                firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;

                                if (firebaseUser.isEmailVerified()) {
                                    loadUserData(firebaseUser.getUid());
                                } else {
                                    loading.dismiss();
                                    UiHelper.toast(Login.this, "Please Verify Your Email");
                                }

                            }

                        }).addOnFailureListener(e -> {

                            Log.e(TAG, "Exception " + e.getMessage());
                            UiHelper.toast(Login.this, e.getMessage());
                            loading.dismiss();

                        });

            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.getMessage());
                loading.dismiss();
            }

        });

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
          firebaseUser = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "User signed in with UID: " + firebaseUser.getUid());
            } else {
                // No user is signed in
                Log.d(TAG, "No user signed in.");
            }
        };

        auth.addAuthStateListener(authStateListener);


    }

    private void loadUserData(String id) {
        userId=id;

        Log.d(TAG,"Method LoadUserData() user ID :"+id);

        if (id != null) {
            try {
                database.getReference(FirebaseConstant.USER)
                        .child(id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                        user = snapshot.getValue(User.class);
                                        saveUserPreference(user);
                                        loading.dismiss();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                        Log.d(TAG, "User data: " +snapshot.getValue().toString());
                                } else {
                                    Log.d(TAG, "Snapshot does not exist");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Error " + error.toString());
                                loading.dismiss();
                            }
                        });

            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.getLocalizedMessage());
                loading.dismiss();
            }
        } else {
            Log.e(TAG, "User ID is null");
            loading.dismiss();
        }
    }



    private void saveUserPreference(User user) {
        if (user != null) {
            sharedPref.putString(SharedPref.PREF_USER, "user", gson.toJson(user));
            Log.d(TAG, "User data saved to preferences."+"\n user ID :"+userId);
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        authHelper.clear();
    }
}