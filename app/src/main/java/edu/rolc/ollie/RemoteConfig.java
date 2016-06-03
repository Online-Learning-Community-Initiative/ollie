package edu.rolc.ollie;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: For now we will hard code the subject hierarchy
 *       Later, we can consider using Firebase Remote Config
 */
public class RemoteConfig {
    private static List<String> subjects;

    static {
        subjects = Arrays.asList("Maths", "Physics", "Chemistry", "English");
    }

    public static List<String> getSubjects() {
        return subjects;
    }
}
