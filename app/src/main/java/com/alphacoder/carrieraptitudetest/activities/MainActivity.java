package com.alphacoder.carrieraptitudetest.activities;

import android.os.Bundle;
import android.util.Log;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ActivityMainBinding;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLog";
    private ActivityMainBinding binding;
    private Gson gson;
    private SharedPref sharedPref;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(this.getColor(R.color.white));

        // Initialize Firebase UID
        try {
            auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
                uid = firebaseUser.getUid();
                Log.d(TAG, "User is signed in with UID: " + uid);
            } else {
                Log.d(TAG, "No user is currently signed in.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Auth Exception: " + e.getLocalizedMessage());
        }

        // Initialize binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Gson and SharedPref
        gson = new Gson();
        sharedPref = new SharedPref(this);

        // Retrieve user data from SharedPreferences
        User user = getUserPreference();

        if (user != null) {
            DataHolder.user = user;
            DataHolder.uId = user.getId();
            Log.d(TAG, "User loaded from preferences: " + user.getName());
        } else {
            Log.e(TAG, "User is null");
            // Handle user not being found in SharedPreferences
        }

        // Setup navigation
        setupNavigation();
    }

    private User getUserPreference() {
        String userJson = sharedPref.getString(SharedPref.PREF_USER, "user");
        if (userJson.isEmpty()) {
            Log.e(TAG, "User JSON string is empty.");
            return null;
        }

        try {
            return gson.fromJson(userJson, User.class);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing User JSON: " + e.getMessage());
            return null;
        }
    }

    private void setupNavigation() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_quiz, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
