package edu.rolc.ollie;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContentDB {
    private static FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();

    public static boolean addNewContent(String filename) {
        DatabaseReference contentdb = cDatabase.getReference("contents");
        String key = contentdb.push().getKey();
        Content content = new Content(key, filename);
        contentdb.child(key).setValue(content);
        return true;
    }

    public static boolean retrieveContentForSubject(String sub) {
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
