package edu.rolc.ollie;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ContentDB {
    private static final String TAG = "Firebase";
    public static String root = "Root";

    private static FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
    private static HashMap<String, List<String>> topics;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        topics = new HashMap<String, List<String>>() {{
            put(root, Arrays.asList("Maths", "Physics", "Chemistry", "English"));
            put("Maths", Arrays.asList("Addition", "Subtraction", "Logarithm", "Calculus"));
            put("Physics", Arrays.asList("Friction", "Newton", "Kinematics", "Thermodynamics"));
            put("Chemistry", Arrays.asList("Physical", "Oraganic", "Inorganic"));
            put("English", Arrays.asList("Grammar"));
        }};
    }

    public static boolean containsTopic(String topic) {
        return topics.containsKey(topic);
    }

    public static List<String> getTopics(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic);
        } else {
            Log.w(TAG, "Topic:" + topic + " doesn't exist, returning root!");
            return topics.get(root);
        }
    }
}
