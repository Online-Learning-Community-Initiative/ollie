package edu.rolc.ollie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String THIS_APP = "OLLiE";
    private static final String MY_PREFS = "ollie_prefs";
    private static final String PREFS_LAYOUT = "layout";

    ContentFragment contentFragment;
    private int lastItem = -1;
    private int curItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get last saved layout from cache
        SharedPreferences shprefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        this.curItem = shprefs.getInt(PREFS_LAYOUT, -1);

        final MainActivity curActivity = this;
        ContentDB.getTopics(ContentDB.root, new TopicResponseCallback() {
            @Override
            public void onReceivingResponse(List<String> topics) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // TODO: 6/29/2016 Make sure that we use the correct pointer
                // TODO: -- to content fragment when activity rotates etc.
                curActivity.contentFragment = new ContentFragment();
                curActivity.contentFragment.setContent(topics);
                curActivity.contentFragment.setCurView(curActivity.curItem);
                fragmentTransaction.add(R.id.fragment, curActivity.contentFragment);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences shprefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        shprefs.edit().putInt(PREFS_LAYOUT, this.curItem).commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.lastItem != -1) {
            menu.findItem(this.lastItem).setVisible(true);
        }

        if (this.curItem != -1) {
            menu.findItem(this.curItem).setVisible(false);
        } else {
            this.curItem = R.id.listview;
            menu.findItem(this.curItem).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.recordButton) {
            CameraHelper.captureCamera(this, CameraHelper.MEDIA_TYPE_VIDEO);
        } else if (item.getItemId() == R.id.captureButton) {
            CameraHelper.captureCamera(this, CameraHelper.MEDIA_TYPE_IMAGE);
        } else if (item.getItemId() == R.id.captureButton) {
            DocHelper.performFileSearch(this);
        } else {
            this.lastItem = this.curItem;
            this.curItem = item.getItemId();
            this.contentFragment.setCurView(this.curItem);
            getFragmentManager()
                    .beginTransaction()
                    .detach(this.contentFragment)
                    .attach(this.contentFragment)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraHelper.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE ||
                requestCode == CameraHelper.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            CameraHelper.onActivityResult(this, requestCode, resultCode, data);
            ContentDB.uploadFile(this, data);
        } else if (requestCode == DocHelper.READ_REQUEST_CODE) {
            ContentDB.uploadFile(this, data);
        } else {
            Log.w(THIS_APP, "Unknown activity result!");
        }
    }
}
