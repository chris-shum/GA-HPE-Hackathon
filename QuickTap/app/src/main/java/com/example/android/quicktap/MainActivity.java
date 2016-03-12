package com.example.android.quicktap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    EditText mMainEditText;
    String mBeerToDisplay;
    Toolbar mToolbar;
    ImageView mToolbarCamera;
    ImageView mToolbarBarcode;
    ImageView mToolbarMicrophone;
    ImageView mToolbarList;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainEditText = (EditText) findViewById(R.id.mainActivity_EditText);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);

        mToolbarCamera = (ImageView) findViewById(R.id.toolbarCamera);
        mToolbarBarcode = (ImageView) findViewById(R.id.toolbarBarcode);
        mToolbarMicrophone = (ImageView) findViewById(R.id.toolbarMicrophone);
        mToolbarList = (ImageView) findViewById(R.id.toolbarList);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeerToDisplay = mMainEditText.getText().toString();
                if (mBeerToDisplay.isEmpty()){
                    mMainEditText.setError("Please fill in");
                }else {
                Intent intent = new Intent(MainActivity.this, LargeDisplayActivity.class);
                intent.putExtra("BeerToDisplayKey", mBeerToDisplay);
                startActivity(intent);
                }
            }
        });

        mToolbarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
