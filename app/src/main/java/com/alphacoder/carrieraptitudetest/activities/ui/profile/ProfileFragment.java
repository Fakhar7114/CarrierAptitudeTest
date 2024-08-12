package com.alphacoder.carrieraptitudetest.activities.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.FragmentProfileBinding;
import com.alphacoder.carrieraptitudetest.helpers.DataHolder;
import com.alphacoder.carrieraptitudetest.helpers.ImageHelper;
import com.alphacoder.carrieraptitudetest.helpers.SharedPref;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.User;
import com.alphacoder.carrieraptitudetest.register.EditProfile;
import com.alphacoder.carrieraptitudetest.register.Login;
import com.alphacoder.carrieraptitudetest.viewModels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    String TAG = "ProfileFragment";
    FragmentProfileBinding binding;
    User user;
    String uid = "";
    FirebaseAuth auth;
    ProfileViewModel profileViewModel;
    SharedPref sharedPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        user = new User();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sharedPref=new SharedPref(requireContext());

        user=DataHolder.user;

        if (user != null) {
            try {

                Glide.with(requireContext())
                        .load(user.getImage())
                        .placeholder(R.drawable.ic_empty_bg)
                        .into(binding.imgProfile);

                binding.tvName.setText(user.getName());
                binding.tvEmail.setText(user.getEmail());
                binding.tvGender.setText(user.getGender());
                binding.tvEducation.setText(user.getEducation());

                Log.d(TAG,"user data :"+user.getName());

            } catch (Exception e) {
                Log.e(TAG, "Exception   " + e.getMessage());

            }
        }


        // Initializing objects




        // Logging out
        binding.btnLogout.setOnClickListener(v -> logoutDialog());

        // Edit profile
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user!=null){
                    EditProfile.user=user;
                    startActivity(new Intent(requireContext(), EditProfile.class));
                }
            }
        });


        return binding.getRoot();
    }

    private void logoutDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("By clicking on Okay button you will be logout from the app," +
                        "and you will also lost you app setting and your saved info. ")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        startActivity(new Intent(requireContext(), Login.class));
                        sharedPref.clear(SharedPref.PREF_USER);
                        requireActivity().finish();
                    }
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(getResources().getColorStateList(R.color.white));
        dialog.getWindow().setBackgroundDrawable(shapeDrawable);
        dialog.show();

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}