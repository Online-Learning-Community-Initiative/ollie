package edu.rolc.ollie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContentFragment contentFragment;
    private int lastItem = -1;
    private int curItem = -1;

    private static final String thisApp = "OLLiE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Let's get the root subjects (topics) first
        final MainActivity curActivity = this;
        ContentDB.getTopics(ContentDB.root, new TopicResponseCallback() {
            @Override
            public void onReceivingResponse(List<String> topics) {
                // Content Fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                curActivity.contentFragment = new ContentFragment();
                curActivity.contentFragment.setContent(topics);
                curActivity.contentFragment.setCurView(curActivity.curItem);
                fragmentTransaction.add(R.id.fragment, curActivity.contentFragment);
                fragmentTransaction.commit();
            }
        });
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.recordbutton) {
            CameraHelper.recordVideo(this);
        } else {
            Log.d(thisApp, "" + item.getTitle());
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
        if (requestCode == CameraHelper.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            CameraHelper.onActivityResult(this, requestCode, resultCode, data);
            ContentDB.uploadFile(this, data);
        } else {
            Log.w(thisApp, "Unknown activity result!");
        }
    }
}
