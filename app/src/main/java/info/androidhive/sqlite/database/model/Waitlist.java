package info.androidhive.sqlite.database.model;

/**
 * Created by Divya.
 */

public class Waitlist {
    public static final String TABLE_NAME = "MC";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String note;
    private String priority;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_PRIORITY + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Waitlist() {
    }

    public Waitlist(int id, String note,  String priority, String timestamp) {
        this.id = id;
        this.note = note;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
