package com.example.android.quicktap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.io.File;
import com.example.android.quicktap.BreweryDbApi.Beer;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SpeechSearch.OnSpeechSearchResultListener {


    //database
    //camera
    //text

    EditText mMainEditText;
    String mBeerToDisplay;
    Toolbar mBottomToolbar;
    ImageView mToolbarCamera;
    ImageView mToolbarMicrophone;
    ImageView mToolbarList;
    Toolbar mTopToolBar;
    Window mWindow;
    CoordinatorLayout mCoordinatorLayout;
    AlertDialog mAudioRecordingDialog;
    ProgressBar mProgressBar;
    SpeechSearch mSpeechSearch;
    ImageView mMainImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainEditText = (EditText) findViewById(R.id.mainActivity_EditText);
        mMainImage = (ImageView) findViewById(R.id.mainImage);
        mMainImage.setImageResource(R.drawable.main);

        mBottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);
        mBottomToolbar.setContentInsetsAbsolute(0, 0);
        mTopToolBar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolBar.setTitleTextColor(getResources().getColor(R.color.colorIconBorder));


        mToolbarCamera = (ImageView) findViewById(R.id.toolbarCamera);
        mToolbarMicrophone = (ImageView) findViewById(R.id.toolbarMicrophone);
        mToolbarList = (ImageView) findViewById(R.id.toolbarList);

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        View layout = findViewById(R.id.clickOnContent);

        mMainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (actionId) {
                        case KeyEvent.KEYCODE_ENTER:
                            mBeerToDisplay = mMainEditText.getText().toString();
                            if (mBeerToDisplay.isEmpty()) {
                                mMainEditText.requestFocus();
                                mMainEditText.setError("Please fill in");
                            } else {
                                Intent intent = new Intent(MainActivity.this, LargeDisplayActivity.class);
                                intent.putExtra("BeerToDisplayKey", mBeerToDisplay);
                                startActivity(intent);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

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
                File directory = Environment.getExternalStorageDirectory();
                File imagesDirectory = new File(directory, "/QuickTap/Camera/");
                imagesDirectory.mkdirs();
                File file = new File(imagesDirectory, "QuickTap.jpg");

//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Uri outputFileUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                Log.d("photo", "path" + file);
                startActivityForResult(intent, 12345);
            }
        });

        mMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mMainImage.setVisibility(View.GONE);
            }
        });

        mToolbarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        mToolbarMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechSearch = new SpeechSearch(MainActivity.this);

                mAudioRecordingDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Say beer name")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // this triggers call to speech recognition api
                                mSpeechSearch.stopRecording();
                                // when that completes, it triggers call to beer api
                                // when that completes, it calls onSpeechSearchResult() below

                                mProgressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .create();

                mAudioRecordingDialog.show();
                mSpeechSearch.startRecording();
            }
        });
    }

    @Override
    public void onSpeechSearchResult(String queryText, final List<Beer> beers) {
        mSpeechSearch.release();
        mSpeechSearch = null;

        mProgressBar.setVisibility(View.GONE);

        if (beers.size() > 0) {
            QuickTapSQLiteOpenHelper helper = QuickTapSQLiteOpenHelper.getInstance(MainActivity.this);
            final long searchId = helper.addSearch(queryText);
            for (Beer beer : beers) {
                helper.addResult((int) searchId, beer.getDisplayName());
            }

            //launch notification that takes user to search results
            Intent intent = new Intent(MainActivity.this, ResultsListActivity.class);
            intent.putExtra(QuickTapSQLiteOpenHelper.RESULTS_SEARCH_ID, searchId);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.drawable.ic_beer)
                    .setContentTitle("QuickTap: New search results")
                    .setContentText("Review results for \"" + queryText + "\"")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());

            Snackbar snack = Snackbar.make(mCoordinatorLayout, "Search completed", Snackbar.LENGTH_LONG)
                    .setAction("View Search Results", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NotificationManager manager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.cancel(notificationId);

                            Intent intent = new Intent(MainActivity.this, ResultsListActivity.class);
                            intent.putExtra(QuickTapSQLiteOpenHelper.RESULTS_SEARCH_ID, searchId);
                            startActivity(intent);
                        }
                    });

            View view = snack.getView();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.CENTER;
            view.setLayoutParams(params);
            snack.show();
        } else {
            Toast.makeText(MainActivity.this, "No results. Sorry!", Toast.LENGTH_SHORT).show();
        }
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
