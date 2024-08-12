package com.alphacoder.carrieraptitudetest.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.adapters.RecordsAdapter;
import com.alphacoder.carrieraptitudetest.databinding.ActivityViewRecordBinding;
import com.alphacoder.carrieraptitudetest.helpers.NetworkConnection;
import com.alphacoder.carrieraptitudetest.helpers.UiHelper;
import com.alphacoder.carrieraptitudetest.models.ResultDetail;
import com.alphacoder.carrieraptitudetest.progresssBar.Loading;
import com.alphacoder.carrieraptitudetest.viewModels.RecordViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ViewRecord extends AppCompatActivity {

    String TAG = "ViewRecords";
    ActivityViewRecordBinding binding;
    RecordsAdapter recordsAdapter;
    List<ResultDetail> records;
    RecordViewModel recordViewModel;
    Loading loading;
    String KEY_RECORDS = "records_list";
    String KEY_CALLED = "is_called";
    boolean isCalled = false;
    Gson gson;
    Map<String ,ResultDetail> mapRecords;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityViewRecordBinding.inflate(getLayoutInflater());
        recordViewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        setContentView(binding.getRoot());

        // Initializing objects
        gson=new Gson();
        mapRecords=new HashMap<>();

        // setup Recyclerview
        recyclerViewSetup();

        if (bundle != null) {
            isCalled = bundle.getBoolean(KEY_CALLED);
            String jsonRecords=bundle.getString(KEY_RECORDS);

            if (jsonRecords != null) {
                mapRecords=gson.fromJson((String) bundle.get(KEY_RECORDS),new TypeToken<Map<String,ResultDetail>>(){}.getType());
                if (mapRecords!=null){
                    recordsAdapter.setRecords(getList());
                }
                Log.d(TAG,"Size of Map "+mapRecords.size());

            }

        }


        // Load Records
        loadRecords();


        // Checking for time out
        recordViewModel.isTimedOut.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean timeout) {

                if (timeout){
                    UiHelper.toast(ViewRecord.this,"Time outed");
                }

            }
        });


        recordViewModel.isData.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.tvNoRecords.setVisibility(View.GONE);
                loading.dismiss();

            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvNoRecords.setVisibility(View.VISIBLE);
                        binding.btnRefresh.setVisibility(View.GONE);
                        loading.dismiss();

                    }
                }, 500);
            }

        });









        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.isAvailable(ViewRecord.this)) {
                    loading=new Loading(ViewRecord.this);
                    loadRecords();
                } else {
                    UiHelper.toast(ViewRecord.this, "No Internet Connection");
                }
            }
        });

        binding.btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void recyclerViewSetup() {
        records=new ArrayList<>();
        recordsAdapter = new RecordsAdapter(this, records);
        binding.rvRecords.setAdapter(recordsAdapter);
    }

    private List<ResultDetail> getList(){
        List<ResultDetail> list=new ArrayList<>();

        mapRecords.forEach(new BiConsumer<String, ResultDetail>() {
            @Override
            public void accept(String s, ResultDetail resultDetail) {
                list.add(resultDetail);
            }
        });

        return list;
    }

    private void loadRecords() {

        loading=new Loading(ViewRecord.this);

        if (isCalled){
            return;
        }
        isCalled=true;




        if (NetworkConnection.isAvailable(ViewRecord.this)) {

            try {
                recordViewModel.getRecords();

                recordViewModel.records.observe(this, new Observer<List<ResultDetail>>() {
                    @Override
                    public void onChanged(List<ResultDetail> resultDetails) {
                        if (resultDetails.isEmpty()) {
                            Log.d(TAG, "Result details list is empty");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    binding.btnRefresh.setVisibility(View.GONE);
                                    binding.tvNoRecords.setVisibility(View.VISIBLE);

                                    loading.dismiss();



                                }
                            }, 5000);

                        } else {
                            recordsAdapter.setRecords(resultDetails);
                            Log.d(TAG, "Size of list " + resultDetails.size());
                            binding.btnRefresh.setVisibility(View.GONE);
                            binding.tvNoRecords.setVisibility(View.GONE);
                            loading.dismiss();
                            Log.d(TAG,"Size of Result list :"+resultDetails.size());
                            Log.d(TAG,"Size of Records list :"+records.size());
                            for (ResultDetail detail:resultDetails){
                                mapRecords.put(detail.getId(),detail);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception loading records " + e.getMessage());
                binding.btnRefresh.setVisibility(View.VISIBLE);
                loading.dismiss();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    binding.btnRefresh.setVisibility(View.VISIBLE);
                    loading.dismiss();

                    loading.dismiss();

                }
            }, 5000);
        }
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


        if (records!=null && !records.isEmpty()){
            outState.putString(KEY_RECORDS,gson.toJson(mapRecords));
        }



        outState.putBoolean(KEY_CALLED, isCalled);

    }
}