package edu.rolc.ollie;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ContentDB {
    private static final String TAG = "Firebase";
    public static String root = "Root";

    private static FirebaseDatabase cDatabase;
    private static HashMap<String, List<String>> topics;

    static {
        cDatabase = FirebaseDatabase.getInstance();
        cDatabase.setPersistenceEnabled(true);
        topics = new HashMap<String, List<String>>() {{
            put(root, Arrays.asList("Maths", "Physics", "Chemistry", "English"));
            put("Maths", Arrays.asList("Addition", "Subtraction", "Logarithm", "Calculus"));
            put("Physics", Arrays.asList("Friction", "Newton", "Kinematics", "Thermodynamics"));
            put("Chemistry", Arrays.asList("Physical", "Oraganic", "Inorganic"));
            put("English", Arrays.asList("Grammar"));
        }};
    }

    // currently the size of topics is small, therefore,
    // we can afford to do this. A better way would be to set
    // a different onClickListener for this case in the activity
    public static boolean containsTopic(String topic) {
        if (topics.containsKey(topic)) {
            return true;
        }

        for (HashMap.Entry<String, List<String>> entry : topics.entrySet()) {
            if (entry.getValue().contains(topic)) {
                return true;
            }
        }

        return false;
    }

    public static void getTopics(String topic, TopicResponseCallback topicResponseCallback) {
        List<String> newTopics;
        if (topics.containsKey(topic)) {
            topicResponseCallback.onReceivingResponse(topics.get(topic));
            return;
        }

        DatabaseReference dbRefToSubjects = cDatabase.getReference(topic);
        if (dbRefToSubjects == null) {
            Log.w(TAG, "getTopics:" + "Topic " + topic + " not found!");
            return;
        }

        final TopicResponseCallback topicResponseCallbackTemp = topicResponseCallback;
        dbRefToSubjects.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ts = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ts.add(ds.getKey());
                }
                topicResponseCallbackTemp.onReceivingResponse(ts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getTopics:onCancelled", databaseError.toException());
            }
        });
    }

    public static void uploadFileUri(Activity curActivity, Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child((new File("" + fileUri)).getName());

        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(fileUri.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "Unable to upload file: " + fileUri);
            return;
        }

        upload(curActivity, fileRef, stream);
    }

    public static void uploadIntentData(Activity curActivity, android.content.Intent data) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child((new File("" + data.getData())).getName());

        InputStream stream = null;
        try {
            stream = curActivity.getContentResolver().openInputStream(data.getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "Unable to upload file: " + data.getData());
            return;
        }

        upload(curActivity, fileRef, stream);
    }

    private static void upload(final Activity curActivity,
            final StorageReference fileRef, InputStream istream) {
        UploadTask uploadTask = fileRef.putStream(istream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Unable to upload file: " + fileRef);
                Log.w(TAG, "" + exception.getStackTrace());
                Toast.makeText(curActivity, "Error in uploading file to DB!",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "File uploaded: " + taskSnapshot.getMetadata());
                Toast.makeText(curActivity, "File uploaded successfully!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public static boolean addNewContent(String sub, String filename) {
//        DatabaseReference contentdb = cDatabase.getReference(sub);
//        String key = contentdb.push().getKey();
//        Content content = new Content(key, filename);
//        contentdb.child(key).setValue(content);
//        return true;
//    }
}
