package com.example.note_taking_app;

public class Note {
    private int id;
    private String text;
    private String date;

    public Note(int id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
