package com.example.android.quicktap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class LargeDisplayActivity extends AppCompatActivity {

    ImageView mFBShare;
    ImageView mOrder;
    ImageView mSearch;
    ImageView mAdd;
    QuickTapSQLiteOpenHelper mHelper;
    String mUpdatedCount;
    Toolbar mBottomToolbar;
    Toolbar mTopToolbar;
    Window mWindow;
    AutoResizeTextView mAutoResizeTextView;


    String mBeerToDisplay;
    //    AutoResizeTextView mAutoResizeTextView;
    final String DOUBLE_BYTE_SPACE = "\u3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mAutoResizeTextView = (AutoResizeTextView) findViewById(R.id.autoResizeTextView);
//        mAutoResizeTextView.resizeText();

        mAutoResizeTextView = (AutoResizeTextView) findViewById(R.id.autoResizeTextView);
        mHelper = new QuickTapSQLiteOpenHelper(LargeDisplayActivity.this);

        mFBShare = (ImageView) findViewById(R.id.toolbarFBShare);
        mOrder = (ImageView) findViewById(R.id.toolbarOrder);
        mSearch = (ImageView) findViewById(R.id.toolbarSearch);
        mAdd = (ImageView) findViewById(R.id.toolbarAdd);

        mBottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbar);
        mBottomToolbar.setContentInsetsAbsolute(0, 0);
        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTopToolbar.setTitleTextColor(getResources().getColor(R.color.colorIconBorder));
        setTitle("QuickTap");

        mWindow = this.getWindow();
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        mBeerToDisplay = intent.getStringExtra("BeerToDisplayKey");

        String fixString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1
                && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            fixString = DOUBLE_BYTE_SPACE;
        }

        mAutoResizeTextView.setText(fixString + mBeerToDisplay + fixString);
        mFBShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if then statement.  if beer is already there, add 1 to count, else add beer and count set to 1
                String beerNameToAdd = mAutoResizeTextView.getText().toString().toUpperCase();
                Cursor cursor = mHelper.searchBeerList(beerNameToAdd);
                if (cursor.getCount() == 0) {
                    mHelper.addDrink(beerNameToAdd, "1");
                } else {
                    int drinkCount = mHelper.getCountByName(beerNameToAdd);
                    int mUpdatedCount =  drinkCount + 1;
                    mHelper.updateDrinkCount(mUpdatedCount, mBeerToDisplay);
                }
                Toast.makeText(LargeDisplayActivity.this, mBeerToDisplay + " has been added!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
