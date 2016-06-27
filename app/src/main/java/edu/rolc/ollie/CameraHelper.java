package edu.rolc.ollie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHelper {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static String TAG = "CAMERA";
    private static String APP_NAME = "OLLiE";

    private static File getOutputMediaFile(int type) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SDCard not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), APP_NAME);

        // Create the storage directory if it does not exist
        Log.d(TAG, ""+mediaStorageDir);
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d(TAG, "Failed to create directory");
//                return null;
//            }
//        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            Log.w(TAG, "Unknown media file!");
            return null;
        }

        return mediaFile;
    }

    private static Uri getOutputMediaFileUri(int type) {
        File outFile = getOutputMediaFile(type);
        if (outFile == null) {
            return null;
        }

        return Uri.fromFile(outFile);
    }

    public static void recordVideo(Activity curActivity) {
        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        if (fileUri == null) {
            Log.w(TAG, "Failed to get path to media file!");
            Toast.makeText(curActivity, "Error in writing to SDCard!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (intent.resolveActivity(curActivity.getPackageManager()) != null) {
            curActivity.startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        } else {
            Log.w(TAG, "Failed to resolve Video activity!");
            Toast.makeText(curActivity, "Error in capturing video!", Toast.LENGTH_LONG).show();
        }
    }

    public static void onActivityResult(Activity curActivity, int requestCode,
                                        int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(curActivity, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(curActivity, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }
}
