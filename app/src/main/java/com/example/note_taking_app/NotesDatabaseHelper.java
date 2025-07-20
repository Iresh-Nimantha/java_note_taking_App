package com.example.note_taking_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    private static final String COL_ID = "id";
    private static final String COL_TEXT = "text";
    private static final String COL_DATE = "date";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEXT + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Add new note with text and date
    public boolean addNote(String text, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEXT, text);
        values.put(COL_DATE, date);
        long result = db.insert(TABLE_NOTES, null, values);
        db.close();
        return result != -1;
    }

    // Update note text and date by id
    public boolean updateNote(int id, String newText, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEXT, newText);
        values.put(COL_DATE, newDate);
        int rows = db.update(TABLE_NOTES, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // Get all notes, most recent first
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null, COL_ID + " DESC");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_ID);
            int textIndex = cursor.getColumnIndex(COL_TEXT);
            int dateIndex = cursor.getColumnIndex(COL_DATE);
            do {
                int id = (idIndex != -1) ? cursor.getInt(idIndex) : 0;
                String text = (textIndex != -1) ? cursor.getString(textIndex) : "";
                String date = (dateIndex != -1) ? cursor.getString(dateIndex) : "";
                notes.add(new Note(id, text, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }


    // Delete note by id
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
