package com.example.android.quicktap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class LargeDisplayActivity extends AppCompatActivity implements AutoResizeTextView.OnTextResizeListener{

    String mBeerToDisplay;
    AutoResizeTextView mAutoResizeTextView;
    final String DOUBLE_BYTE_SPACE = "\u3000";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAutoResizeTextView = (AutoResizeTextView) findViewById(R.id.autoResizeTextView);
        mAutoResizeTextView.resizeText();



        Intent intent = getIntent();
        mBeerToDisplay = intent.getStringExtra("BeerToDisplayKey");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(LargeDisplayActivity.this, MainActivity.class);
                startActivity(intentBack);
            }
        });

        String fixString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1
                && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            fixString = DOUBLE_BYTE_SPACE;
        }
        mAutoResizeTextView.setText(fixString + mBeerToDisplay + fixString);

    }

    @Override
    public void onTextResize(TextView textView, float oldSize, float newSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
    }
}
