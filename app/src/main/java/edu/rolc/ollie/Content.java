package edu.rolc.ollie;

public class Content {
    private String id;
    private String filename;

    public Content() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Content(String id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
