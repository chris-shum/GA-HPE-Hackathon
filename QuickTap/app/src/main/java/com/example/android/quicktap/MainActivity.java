package com.example.android.quicktap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //database
    //camera
    //text

    EditText mMainEditText;
    String mBeerToDisplay;
    Toolbar mBottomToolbar;
    ImageView mToolbarCamera;
    ImageView mToolbarBarcode;
    ImageView mToolbarMicrophone;
    ImageView mToolbarList;
    Toolbar mTopToolBar;
    Window mWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainEditText = (EditText) findViewById(R.id.mainActivity_EditText);

        mBottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);
        mBottomToolbar.setContentInsetsAbsolute(0, 0);
        mTopToolBar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolBar.setTitleTextColor(getResources().getColor(R.color.colorIconBorder));

        mToolbarCamera = (ImageView) findViewById(R.id.toolbarCamera);
        mToolbarBarcode = (ImageView) findViewById(R.id.toolbarBarcode);
        mToolbarMicrophone = (ImageView) findViewById(R.id.toolbarMicrophone);
        mToolbarList = (ImageView) findViewById(R.id.toolbarList);

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));



        View layout = findViewById(R.id.clickOnContent);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeerToDisplay = mMainEditText.getText().toString();
                if (mBeerToDisplay.isEmpty()) {
                    mMainEditText.requestFocus();
                    mMainEditText.setError("Please fill in");

                } else {
                    Intent intent = new Intent(MainActivity.this, LargeDisplayActivity.class);
                    intent.putExtra("BeerToDisplayKey", mBeerToDisplay);
                    startActivity(intent);
                }
            }
        });

        mToolbarCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().getPath() + "/QuickTap/Camera/";
                File file = new File(path,"QuickTap.jpg");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri outputFileUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                Log.d("photo", "path"+file);
                startActivityForResult(intent, 12345);
            }
        });

        mToolbarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
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
