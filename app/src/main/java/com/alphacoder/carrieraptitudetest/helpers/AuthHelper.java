package com.alphacoder.carrieraptitudetest.helpers;



import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthHelper {

    String TAG = "AuthHelper";
    ExecutorService executor;
    FirebaseAuth auth;
    FirebaseUser user;
    boolean isSent;

    public AuthHelper() {
        executor = Executors.newSingleThreadExecutor();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        isSent = false;

    }

    public String getId(){
        return auth.getUid();
    }

    public boolean isVerified(){
        return user.isEmailVerified();
    }


    public void login(String email, String password, AuthCallback callback) {

        executor.submit(() -> {

            try {

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful() && task.isComplete()) {
                                callback.success(true);
                            } else {
                                callback.success(false);
                            }


                        }).addOnFailureListener(e -> {
                            callback.failure(e.getMessage());
                        });

            } catch (Exception e) {
                callback.failure(e.getMessage());
            }

        });

    }

    public void signUp(String email, String pass, AuthCallback callback) {


        executor.submit(() -> {

            try {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful()) {

                        if (sendVerificationEmail()){
                            callback.success(true);
                        }


                    } else {
                        callback.success(false);
                    }
                }).addOnFailureListener(e -> {
                    callback.failure(e.getMessage());
                });

            } catch (Exception e) {
                callback.failure(e.getMessage());

            }

        });


    }

    public void resetPassword(String email, AuthCallback callback) {

        executor.submit(() -> {

            try {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {

                            if (task.isComplete() && task.isSuccessful()) {
                                callback.success(true);
                            } else {
                                callback.success(false);
                            }
                        }).addOnFailureListener(e -> callback.failure(e.getMessage()));

            } catch (Exception e) {
                callback.failure(e.getMessage());
            }

        });


    }

    public boolean sendVerificationEmail() {

        executor.submit(() -> {

            try {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {

                            isSent = task.isSuccessful() && task.isComplete();
                            Log.d(TAG,"Email Sent "+isSent);

                        })
                        .addOnFailureListener(e -> {

                            Log.e(TAG,"Exception"+e.getMessage());

                            isSent = false;

                        });
            } catch (Exception e) {
                isSent = false;
                Log.e(TAG,"Exception"+e.getMessage());
            }
        });

        return isSent;


    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;

    }
    public void clear(){
        executor.shutdown();
        auth=null;
        user=null;

    }



    public void logout() {
        auth.signOut();
    }

    public interface AuthCallback {

        void success(boolean value);

        void failure(String message);
    }


}
