
package info.androidhive.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.database.model.Waitlist;

    /**
     * Created by Divya.
     */

    public class WaitListDatabaseHelper extends SQLiteOpenHelper {

        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "wait_db";


        public WaitListDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {

            // create notes table
            db.execSQL(Waitlist.CREATE_TABLE);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + Waitlist.TABLE_NAME);

            // Create tables again
            onCreate(db);
        }

        public long insertNote(String table_name, String note, String priority) {
            // get writable database as we want to write data
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            // `id` and `timestamp` will be inserted automatically.
            // no need to add them
            values.put(Waitlist.COLUMN_NOTE, note);
            values.put(Waitlist.COLUMN_PRIORITY, priority);

            // insert row
            long id = db.insert(table_name, null, values);

            // close db connection
            db.close();

            // return newly inserted row id
            return id;
        }

        public Waitlist getNote(String table_name, long id) {
            // get readable database as we are not inserting anything
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(table_name,
                    new String[]{Waitlist.COLUMN_ID, Waitlist.COLUMN_NOTE, Waitlist.COLUMN_PRIORITY, Waitlist.COLUMN_TIMESTAMP},
                    Waitlist.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null)
                cursor.moveToFirst();

            // prepare note object
            Waitlist note = new Waitlist(
                    cursor.getInt(cursor.getColumnIndex(Waitlist.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_NOTE)),
                    cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_PRIORITY)),
                    cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_TIMESTAMP)));

            // close the db connection
            cursor.close();

            return note;
        }

        public List<Waitlist> getAllNotes(String table_name) {
            List<Waitlist> notes = new ArrayList<>();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + table_name + " ORDER BY " +
                    Waitlist.COLUMN_TIMESTAMP + " DESC";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Waitlist note = new Waitlist();
                    note.setId(cursor.getInt(cursor.getColumnIndex(Waitlist.COLUMN_ID)));
                    note.setNote(cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_NOTE)));
                    note.setPriority(cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_PRIORITY)));
                    note.setTimestamp(cursor.getString(cursor.getColumnIndex(Waitlist.COLUMN_TIMESTAMP)));

                    notes.add(note);
                } while (cursor.moveToNext());
            }

            // close db connection
            db.close();

            // return notes list
            return notes;
        }

        public int getNotesCount(String table_name) {
            String countQuery = "SELECT  * FROM " + table_name;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);

            int count = cursor.getCount();
            cursor.close();


            // return count
            return count;
        }

        public int updateNote(String table_name,Waitlist note) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Waitlist.COLUMN_NOTE, note.getNote());
            values.put(Waitlist.COLUMN_PRIORITY, note.getPriority());

            // updating row
            return db.update(table_name, values, Waitlist.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
        }

        public void deleteNote(String table_name,Waitlist note) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(table_name, Waitlist.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(note.getId())});
            db.close();
        }
    }

