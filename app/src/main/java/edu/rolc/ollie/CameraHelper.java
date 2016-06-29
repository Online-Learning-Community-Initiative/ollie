package edu.rolc.ollie;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private static String APP_NAME = MainActivity.THIS_APP;
    private static String TAG = "CAMERA";

    private static Uri fileUri;

    private static File getOutputMediaFile(int type) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SDCard not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), APP_NAME);

        // Create the storage directory if it does not exist
        Log.d(TAG, "" + mediaStorageDir);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

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

    public static void captureCamera(Activity curActivity, int requestCode) {
        PackageManager packageManager = curActivity.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == false) {
            Toast.makeText(curActivity,
                    "This device does not have a camera!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        fileUri = getOutputMediaFileUri(requestCode);
        if (fileUri == null) {
            Log.w(TAG, "Failed to get path to media file!");
            Toast.makeText(curActivity, "Error in writing to SDCard!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent;
        int intentRequestCode;
        if (requestCode == MEDIA_TYPE_VIDEO) {
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intentRequestCode = CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE;
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentRequestCode = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(curActivity.getPackageManager()) != null) {
            curActivity.startActivityForResult(intent, intentRequestCode);
        } else {
            Log.w(TAG, "Failed to resolve activity!");
            Toast.makeText(curActivity, "Error in capturing from camera!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void onVideoActivityResult(Activity curActivity, int requestCode,
                                             int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Video captured and saved to fileUri specified in the Intent
            Toast.makeText(curActivity, "Video saved to:\n" +
                    data.getData(), Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled the video capture
        } else {
            // Video capture failed, advise user
        }
    }

    public static Uri onImageActivityResult(Activity curActivity, int requestCode,
                                             int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Toast.makeText(curActivity, "Image saved to:\n" +
                    fileUri, Toast.LENGTH_SHORT).show();
            return fileUri;
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled the image capture
            return null;
        } else {
            // Image capture failed, advise user
            return null;
        }
    }
}
