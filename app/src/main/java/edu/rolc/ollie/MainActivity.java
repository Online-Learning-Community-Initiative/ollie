package edu.rolc.ollie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    ContentFragment contentFragment;
    private int lastItem = -1;
    private int curItem = -1;

    private static final String thisApp = "OLLiE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                contentFragment.setCurView(curActivity.curItem);
                fragmentTransaction.add(R.id.fragment, contentFragment);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    private static Uri getOutputMediaFileUri(int type){
        File outFile = getOutputMediaFile(type);
        if (outFile == null) {
            return null;
        }

        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(thisApp, "SDCard not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), thisApp);

        // Create the storage directory if it does not exist
        // if (! mediaStorageDir.exists()){
        //     if (! mediaStorageDir.mkdirs()){
        //         Log.d(thisApp, "failed to create directory");
        //         return null;
        //     }
        // }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            Log.w(thisApp, "unknown media file!");
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.recordbutton) {
            Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            if (fileUri == null) {
                Log.w(thisApp, "Failed to get path to media file!");
                Toast.makeText(this, "Error in writing to SDCard!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
            } else {
                Log.w(thisApp, "Failed to resolve Video activity!");
                Toast.makeText(this, "Error in capturing video!",
                        Toast.LENGTH_LONG).show();
            }

            return super.onOptionsItemSelected(item);
        }

        this.lastItem = this.curItem;
        this.curItem = item.getItemId();
        getFragmentManager()
                .beginTransaction()
                .detach(this.contentFragment)
                .attach(this.contentFragment)
                .commit();

        return super.onOptionsItemSelected(item);
    }
}
