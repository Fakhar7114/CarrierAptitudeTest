package com.alphacoder.carrieraptitudetest.progresssBar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ItemLoadingBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class Loading extends AlertDialog {

    private static final String TAG = "Loading";

    ItemLoadingBinding binding;

    public Loading(@NonNull Context context) {
        super(context);
        binding=ItemLoadingBinding.inflate(LayoutInflater.from(context),null,false);
        setView(binding.getRoot());
        setCancelable(false);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        show();
    }
}
