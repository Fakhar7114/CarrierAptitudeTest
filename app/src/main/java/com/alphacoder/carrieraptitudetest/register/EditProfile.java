package com.alphacoder.carrieraptitudetest.register;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.activities.MainActivity;
import com.alphacoder.carrieraptitudetest.constant.FirebaseConstant;
import com.alphacoder.carrieraptitudetest.databinding.ActivityEditProfileBinding;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.EditTextValueChecker;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.helpers.StorageHelper;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class EditProfile extends AppCompatActivity {

    String TAG = "EditProfile";
    ActivityEditProfileBinding binding;
    FirebaseDatabase database;
    StorageHelper storageHelper;
    int IMAGE_REQUEST_CODE = 2;
    Uri image;
    public static User user;
    Loading loading;
    MainActivity mainActivity;
    String uid="";
    private Gson gson;
    private SharedPref sharedPref;
    String imageUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid= FirebaseAuth.getInstance().getUid();

        // setting user data into views
        try {
            Glide.with(this)
                    .load(user.getImage())
                    .placeholder(R.drawable.ic_empty_bg)
                    .into(binding.imgProfile);
            binding.etName.setText(user.getName());
            binding.etEmail.setText(user.getEmail());
            binding.etGender.setHint(user.getGender());
        } catch (Exception e) {
            binding.imgProfile.setImageResource(R.drawable.ic_empty_bg);
            Log.e(TAG, "Exception   " + e.getMessage());

        }

        // Initializing objects
        database = FirebaseDatabase.getInstance();
        storageHelper = new StorageHelper(this);
        mainActivity=new MainActivity();
        gson=new Gson();
        sharedPref=new SharedPref(this);


        // Set gender list
        setGender();


        binding.btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Storage permission and getting image from gallery
                storagePermission();
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(EditProfile.this)) {

                    if (EditTextValueChecker.hasValue(binding.etName) &&
                            EditTextValueChecker.hasValue(binding.etGender)) {

                        String name=binding.etName.getText().toString().trim();
                        String gender=binding.etGender.getText().toString().trim();



                        loading=new Loading(EditProfile.this);

                        if (image!=null){

                            uploadImage(name,gender);

                        }
                        else{
                            updateUserProfile("",name,gender,false);
                        }

                    }

                } else {
                    UiHelper.toast(EditProfile.this, "No Internet Connection");
                }
            }
        });

    }

    private void setGender() {


        String male = this.getString(R.string.male);
        String female = this.getString(R.string.female);

        String[] arrGender;
        if (user.getGender().equals("Male")){
            arrGender = new String[]{
                    male, female
            };
        }
        else{
            arrGender = new String[]{
                    female, male
            };
        }


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


    private void storagePermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getGalleryPhoto();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        if (response.isPermanentlyDenied()) {
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

    private void showPermissionDialog(PermissionToken token) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
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

    private void showSettings() {

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
        dialog.show();
        ;

    }

    private void uploadImage(String name,String gender) {
        storageHelper.uploadFile(image, new StorageHelper.OnFileUploadedListener() {
            @Override
            public void onUploadSuccess(String url) {

                String imageUrl=url!=null ?url:"";
                updateUserProfile(imageUrl,name,gender,true);
                Log.d(TAG, "Image Uploaded url "+url);
            }

            @Override
            public void onUploadFailure(Exception e) {

                Log.e(TAG, "Exception " + e.getMessage());
                loading.dismiss();

            }

            @Override
            public void onUploadProgress(int progress) {
                Log.d(TAG, "Progress " + progress);
            }
        });
    }

    private void updateUserProfile(String url,String name,String gender,boolean isProfile) {

        if (isProfile){
            user.setImage(url);
        }

        user.setName(name);
        user.setGender(gender);
        database.getReference(FirebaseConstant.USER)
                .child(uid)
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loading.dismiss();
                        UiHelper.toast(EditProfile.this,"Profile Updated");
                        saveUserPreference(user);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Exception "+e.getMessage());
                        loading.dismiss();;
                    }
                });

    }
    private void saveUserPreference(User user) {
        if (user != null) {
            sharedPref.putString(SharedPref.PREF_USER, "user", gson.toJson(user));
            Log.d(TAG, "User data saved to preferences.");
        }
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
                        Glide.with(EditProfile.this).clear(binding.imgProfile);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}