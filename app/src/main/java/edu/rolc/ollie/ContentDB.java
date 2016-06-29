package edu.rolc.ollie;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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

    public static void uploadFile(Activity curActivity, android.content.Intent data) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("mountains.mp4");

        InputStream stream = null;
        try {
            stream = curActivity.getContentResolver().openInputStream(data.getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        UploadTask uploadTask = mountainsRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
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
