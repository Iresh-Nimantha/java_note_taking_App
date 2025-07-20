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

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Notes table
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEXT + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(CREATE_NOTES_TABLE);

        // Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // NOTES methods

    public boolean addNote(String text, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEXT, text);
        values.put(COL_DATE, date);
        long result = db.insert(TABLE_NOTES, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateNote(int id, String newText, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEXT, newText);
        values.put(COL_DATE, newDate);
        int rows = db.update(TABLE_NOTES, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

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

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // USER methods

    // Register a new user (returns false if username exists)
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            // Username already exists, or insert error
        }
        db.close();
        return result != -1;
    }

    // Authenticate a user (login)
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Check if a username is already registered (for signup check)
    public boolean isUserRegistered(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                COL_USERNAME + "=?",
                new String[]{username},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Check if at least one user is registered (for splash/startup logic)
    public boolean hasAnyUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        boolean hasUser = false;
        if(cursor.moveToFirst()) {
            hasUser = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();
        return hasUser;
    }
}
