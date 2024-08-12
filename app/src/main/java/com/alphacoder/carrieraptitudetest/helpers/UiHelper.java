package com.alphacoder.carrieraptitudetest.helpers;


import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class UiHelper {

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void snackBar(Context context, View view, String message){
        Snackbar.make(context,view,message,Snackbar.LENGTH_LONG).show();
    }




}

