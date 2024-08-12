package com.alphacoder.carrieraptitudetest.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class StorageHelper {

    String TAG = "StorageHelper";
    String FOLDER="user_images";
    String childName="";
    FirebaseStorage storage;




    public StorageHelper(Context context) {
        storage = FirebaseStorage.getInstance();
        childName= UUID.randomUUID().toString();
    }


    public void uploadFile(Uri file, OnFileUploadedListener listener) {

        Log.e(TAG, "child name " + childName + "  " + file);

        try {
            StorageReference storageReference = storage.getReference().child(FOLDER).child(childName);

            storageReference.putFile(file).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.isComplete()) {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (!uri.toString().isEmpty()) {
                            listener.onUploadSuccess(uri.toString());

                        }
                        else {
                            listener.onUploadFailure(task.getException());
                        }


                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    listener.onUploadFailure(e);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (double) (snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount();
                    listener.onUploadProgress((int) progress);

                }
            });
        } catch (Exception e) {
            listener.onUploadFailure(e);
        }

    }


    public interface OnFileUploadedListener {
        void onUploadSuccess(String url);

        void onUploadFailure(Exception e);

        void onUploadProgress(int progress);
    }
}
