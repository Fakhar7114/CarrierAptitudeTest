package com.alphacoder.carrieraptitudetest.register;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ActivitySignUpBinding;
import com.alphacoder.carrieraptitudetest.helpers.EditTextValueChecker;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.StorageHelper;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;


public class SignUp extends AppCompatActivity {

    String TAG="Signup";
    ActivitySignUpBinding binding;
    int IMAGE_REQUEST_CODE = 1;
    Uri image;
    User user;
    String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        uid= FirebaseAuth.getInstance().getUid();

        // Setup Gender dropDown
        setGender();

        // Setup Education Levels
        setEducationLevels();

        // Setup clickable text for Sign in
        setSignInClickable();

        // initialize  user object
        user = new User();



        binding.btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storagePermission();
            }
        });



        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (NetworkConnection.isAvailable(SignUp.this)){

                if (EditTextValueChecker.hasValue(binding.etName)
                && EditTextValueChecker.hasValue(binding.etEmail))
                {

                    String name = binding.etName.getText().toString();
                    String email = binding.etEmail.getText().toString();

                    String education = binding.etEducation.getText().toString();
                    String gender = binding.etGender.getText().toString();

                    if (education.isEmpty()){
                        UiHelper.toast(SignUp.this,"Select your Education");
                        return;
                    }
                    if (gender.isEmpty()){
                        UiHelper.toast(SignUp.this,"Select gender ");
                        return;
                    }

                    user.setGender(gender);
                    user.setEmail(email);
                    user.setName(name);
                    user.setEducation(education);
                    user.setImage("");
                    user.setDeviceToken("");
                    user.setId(uid);

                    if (image==null){
                        SetPassword.user=user;
                        SetPassword.uri=null;
                        startActivity(new Intent(SignUp.this, SetPassword.class));
                    }
                    else {
                        SetPassword.user=user;
                        SetPassword.uri=image;
                        startActivity(new Intent(SignUp.this, SetPassword.class));
                    }
                }

            }
            else{
                UiHelper.toast(SignUp.this,"No Internet Connection");
            }




            }
        });

    }

    private void setEducationLevels() {
        String[] arrEducation = {
                "10th Grade",
                "Intermediate",
                "F.A",
                "F.Sc",
                "B.Sc",
                "B.A",
                "B.Com",
                "BBA",
                "M.Sc",
                "M.A",
                "M.Com",
                "MBA"
        };

        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, R.layout.item_drop_down, R.id.tv_dropDown_item, arrEducation);
        binding.etEducation.setAdapter(educationAdapter);
        binding.etEducation.setFocusable(false);
        binding.etEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etEducation.showDropDown();
            }
        });

    }

    private void setGender() {


        String male = this.getString(R.string.male);
        String female = this.getString(R.string.female);

        String[] arrGender = {
                male, female
        };

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.item_drop_down, R.id.tv_dropDown_item, arrGender);
        binding.etGender.setAdapter(genderAdapter);
        binding.etGender.setFocusable(false);
        binding.etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etGender.showDropDown();
            }
        });

    }

    private void setSignInClickable() {
        String text = binding.tvAlreadyHaveAccount.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                Intent i = new Intent(SignUp.this, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, 25, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvAlreadyHaveAccount.setText(spannableString);
        binding.tvAlreadyHaveAccount.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private void storagePermission(){

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getGalleryPhoto();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        if (response.isPermanentlyDenied()){
                            showSettings();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        showPermissionDialog(permissionToken);



                    }
                }).check();
    }

    private void getGalleryPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), IMAGE_REQUEST_CODE);

    }

    private void showPermissionDialog( PermissionToken token){

        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
        builder.setTitle("Permission Needed")
                .setMessage("This app needs to access your storage.")
                .setPositiveButton("Grant", (dialog, which) -> token.continuePermissionRequest())
                .setNegativeButton("Deny", (dialog, which) -> token.cancelPermissionRequest());

        AlertDialog dialog = builder.create();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(getResources().getColorStateList(R.color.white));
        dialog.getWindow().setBackgroundDrawable(shapeDrawable);
        dialog.show();

    }

    private void showSettings(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Permission Denied")
                .setMessage("Permission to access storage is permanently denied.Please go to setting and enable it.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(getResources().getColorStateList(R.color.white));
        dialog.getWindow().setBackgroundDrawable(shapeDrawable);
        dialog.show();;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == IMAGE_REQUEST_CODE) {
                    assert data != null;
                    image = data.getData();

                    if (image != null) {
                        binding.imgProfile.setImageURI(data.getData());

                    } else {
                        UiHelper.toast(this, "No Image Selected");
                        binding.imgProfile.setImageResource(R.drawable.ic_empty_bg);

                    }

                }
            }
        } catch (Exception e) {
            UiHelper.toast(this, "No Image Selected");
            binding.imgProfile.setImageResource(R.drawable.ic_empty_bg);
        }
    }
}
