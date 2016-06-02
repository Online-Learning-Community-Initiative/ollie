package edu.rolc.ollie;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContentDB {
    private static FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();

    public static boolean addNewContent(String sub, String filename) {
        DatabaseReference contentdb = cDatabase.getReference(sub);
        String key = contentdb.push().getKey();
        Content content = new Content(key, filename);
        contentdb.child(key).setValue(content);
        return true;
    }

    public static void retrieveContentForSubject(String sub) {
        DatabaseReference contentdb = cDatabase.getReference(sub);
        contentdb.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get index for given subject
                        Content content = dataSnapshot.getValue(Content.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                        Toast.makeText(mContext, "Failed to load comments.",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // upload and download file
}
