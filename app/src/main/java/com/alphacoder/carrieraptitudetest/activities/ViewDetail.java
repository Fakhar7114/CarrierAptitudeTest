package com.alphacoder.carrieraptitudetest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ActivityViewDetailBinding;
import com.alphacoder.carrieraptitudetest.models.Detail;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.alphacoder.carrieraptitudetest.viewModels.DetailViewModel;

public class ViewDetail extends AppCompatActivity {

    String TAG="ViewDetailLog";
    ActivityViewDetailBinding binding;
    DetailViewModel detailViewModel;
    public static String name;
    Loading loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityViewDetailBinding.inflate(getLayoutInflater());
        detailViewModel=new ViewModelProvider(this).get(DetailViewModel.class);
        setContentView(binding.getRoot());

        // Initializing objects
        loading=new Loading(this);


        // getting detail of Field
        getDetail();


        binding.btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewDetail.this,MainActivity.class));
                finish();
            }
        });

    }

    private void getDetail() {
        detailViewModel.getDetail(name);
        detailViewModel.detail.observe(this, new Observer<Detail>() {
            @Override
            public void onChanged(Detail detail) {
                try {
                    if (detail!=null){

                        loading.dismiss();
                        Log.d(TAG,detail+" \n"+detail.getName()+"\n"+detail.getDescription());

                        binding.tvCareers.setText(getString(R.string.career));
                        binding.tvFieldName.setText(detail.getName());
                        binding.tvFieldDescription.setText(detail.getDescription());

                    }
                    else{
                        Log.e(TAG,"Detail is null");
                    }
                }
                catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                }

           StringBuilder carries=new StringBuilder();
                assert detail != null;
                for (String data:detail.getCareers()){
                carries.append(data).append("\n");
            }
            binding.tvCareerList.setText(carries);
            }
        });
    }
}