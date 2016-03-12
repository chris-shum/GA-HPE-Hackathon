package com.example.android.hpehackathon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hod.api.hodclient.IHODClientCallback;

public class MainActivity extends AppCompatActivity implements IHODClientCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
    }

    @Override
    public void requestCompletedWithContent(String response) {

    }

    @Override
    public void requestCompletedWithJobID(String response) {

    }

    @Override
    public void onErrorOccurred(String errorMessage) {

    }
}
