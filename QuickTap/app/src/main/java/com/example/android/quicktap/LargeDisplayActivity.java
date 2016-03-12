package com.example.android.quicktap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

public class LargeDisplayActivity extends AppCompatActivity {

    String mBeerToDisplay;
    TextView mBeerToDisplayTextView;
    int mViewWidth;
    int mViewHeight;
    TextPaint mTextPaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBeerToDisplayTextView = (TextView) findViewById(R.id.largeTextTextView);

        Intent intent = getIntent();
        mBeerToDisplay = intent.getStringExtra("BeerToDisplayKey");
        mBeerToDisplayTextView.setText(mBeerToDisplay);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(LargeDisplayActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });
    }
}
