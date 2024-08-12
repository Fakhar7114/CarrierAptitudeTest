package com.alphacoder.carrieraptitudetest.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;


public class SharedPref {



    public static String PREF_USER="user";
    public static String PREF_SETTING="setting";

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public SharedPref(Context context) {
        this.context = context;
    }

    public void putString(String prefName, String key, String value) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putBoolean(String prefName, String key, boolean value) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getString(String prefName, String key) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        return sp.getString(key, "");
    }

    public boolean getBoolean(String prefName, String key) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        return sp.getBoolean(key, false);

    }

    public void putLong(String prefName, String key, long value) {
        sp = context.getSharedPreferences(prefName, 0);
        editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String prefName, String key) {
        sp = context.getSharedPreferences(prefName, 0);
        return sp.getLong(key,0);
    }

    public void clear(String prefName) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public void setBoolean(String prefName, String key, boolean value) {
        sp = context.getSharedPreferences(prefName, 0); // 0 for Private Mode
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }
}
