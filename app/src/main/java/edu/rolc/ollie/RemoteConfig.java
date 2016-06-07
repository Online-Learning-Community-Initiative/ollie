package edu.rolc.ollie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: For now we will hard code the subject hierarchy
 *       Later, we can consider using Firebase Remote Config
 */
public class RemoteConfig {
    private static HashMap<String, List<String>> topics;
    private static String root = "Root";

    static {
        topics = new HashMap<String, List<String>>() {{
            put(root, Arrays.asList("Maths", "Physics", "Chemistry", "English"));
            put("Maths", Arrays.asList("Addition", "Subtraction", "Logarithm", "Calculus"));
            put("Physics", Arrays.asList("Friction", "Newton", "Kinematics", "Thermodynamics"));
            put("Chemistry", Arrays.asList("Physical", "Oraganic", "Inorganic"));
            put("English", Arrays.asList("Grammar"));
        }};
    }

    public static List<String> getSubjects() {
        return topics.get(root);
    }

    public static List<String> getTopics(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic);
        } else {
            return topics.get(root);
        }
    }
}
