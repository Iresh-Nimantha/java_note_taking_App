package com.example.note_taking_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteActionListener {
        void onDelete(Note note);
        void onUpdate(Note note);
    }

    private List<Note> noteList;
    private final OnNoteActionListener listener;

    public NoteAdapter(List<Note> noteList, OnNoteActionListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder h, int p) {
        Note n = noteList.get(p);
        h.textViewNote.setText(n.getText());
        h.textViewDate.setText(n.getDate());
        h.btnDelete.setOnClickListener(v -> listener.onDelete(n));
        h.btnUpdate.setOnClickListener(v -> listener.onUpdate(n));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void setNotes(List<Note> notes) {
        this.noteList = notes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNote, textViewDate;
        Button btnDelete, btnUpdate;

        public NoteViewHolder(View v) {
            super(v);
            textViewNote = v.findViewById(R.id.textViewNote);
            textViewDate = v.findViewById(R.id.textViewDate);
            btnDelete = v.findViewById(R.id.btnDelete);
            btnUpdate = v.findViewById(R.id.btnUpdate);
        }
    }
}
