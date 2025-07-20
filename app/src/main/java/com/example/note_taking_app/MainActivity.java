package com.example.note_taking_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesDatabaseHelper dbHelper;
    private NoteAdapter adapter;
    private EditText editTextNote;
    private Button addButton;
    private TextView textViewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new NotesDatabaseHelper(this);
        editTextNote = findViewById(R.id.editTextNote);
        addButton = findViewById(R.id.addButton);
        textViewCount = findViewById(R.id.textViewCount);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(dbHelper.getAllNotes(), new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onDelete(Note note) {
                dbHelper.deleteNote(note.getId());
                refreshNotes();
                showIconToast("Note deleted");
            }

            @Override
            public void onUpdate(Note note) {
                showUpdateDialog(note);
            }
        });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String noteText = editTextNote.getText().toString().trim();
            if (!TextUtils.isEmpty(noteText)) {
                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date());

                boolean inserted = dbHelper.addNote(noteText, currentDate);
                if (inserted) {
                    editTextNote.setText("");
                    refreshNotes();
                    showIconToast("Note saved!");
                } else {
                    showIconToast("Error saving note");
                }
            } else {
                showIconToast("Please enter a note");
                shakeView(editTextNote);
            }
        });

        // LOGOUT BUTTON
        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Clear login state from SharedPreferences
            getSharedPreferences("auth", MODE_PRIVATE)
                    .edit()
                    .putBoolean("signedIn", false)
                    .apply();

            // Go back to SignInActivity and clear back stack
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            showIconToast("Signed out successfully");
        });

        refreshNotes();
    }

    private void refreshNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        adapter.setNotes(notes);

        int count = notes.size();
        String countText = count == 1 ? "1 note" : count + " notes";
        textViewCount.setText(countText);
    }

    private void showUpdateDialog(Note note) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_note, null);

        EditText editTextUpdate = dialogView.findViewById(R.id.editTextUpdate);
        TextView textViewCharCount = dialogView.findViewById(R.id.textViewCharCount);
        TextView textViewOriginalDate = dialogView.findViewById(R.id.textViewOriginalDate);
        TextView textViewPreview = dialogView.findViewById(R.id.textViewPreview);

        editTextUpdate.setText(note.getText());
        editTextUpdate.setSelection(editTextUpdate.getText().length());
        textViewOriginalDate.setText("Created: " + note.getDate());
        updateCharacterCount(editTextUpdate, textViewCharCount);
        updatePreview(editTextUpdate, textViewPreview);

        editTextUpdate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                updateCharacterCount(editTextUpdate, textViewCharCount);
                updatePreview(editTextUpdate, textViewPreview);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        buttonUpdate.setOnClickListener(v -> {
            String updatedText = editTextUpdate.getText().toString().trim();
            if (!TextUtils.isEmpty(updatedText)) {
                if (!updatedText.equals(note.getText())) {
                    String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                            .format(new java.util.Date());
                    boolean updated = dbHelper.updateNote(note.getId(), updatedText, currentDate);
                    if (updated) {
                        refreshNotes();
                        showIconToast("Note updated!");
                        dialog.dismiss();
                    } else {
                        showIconToast("Error updating note");
                    }
                } else {
                    showIconToast("No changes made");
                    dialog.dismiss();
                }
            } else {
                showIconToast("Note cannot be empty");
                editTextUpdate.requestFocus();
                shakeView(editTextUpdate);
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateCharacterCount(EditText editText, TextView charCountView) {
        int length = editText.getText().length();
        String countText = length + " characters";
        charCountView.setText(countText);

        // Change color based on length
        if (length > 500) {
            charCountView.setTextColor(Color.parseColor("#FF5722")); // Red
        } else if (length > 300) {
            charCountView.setTextColor(Color.parseColor("#FF9800")); // Orange
        } else {
            charCountView.setTextColor(Color.parseColor("#757575")); // Gray
        }
    }

    private void updatePreview(EditText editText, TextView previewView) {
        String text = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            String preview = text.length() > 100 ? text.substring(0, 100) + "..." : text;
            previewView.setText("Preview: " + preview);
            previewView.setVisibility(View.VISIBLE);
        } else {
            previewView.setVisibility(View.GONE);
        }
    }

    private void shakeView(View view) {
        view.animate()
                .translationX(-10f)
                .setDuration(50)
                .withEndAction(() -> view.animate()
                        .translationX(10f)
                        .setDuration(50)
                        .withEndAction(() -> view.animate()
                                .translationX(-5f)
                                .setDuration(50)
                                .withEndAction(() -> view.animate()
                                        .translationX(0f)
                                        .setDuration(50)
                                        .start())
                                .start())
                        .start())
                .start();
    }

    // CUSTOM TOAST: NLOGO ICON + ROUND CARD
    private void showIconToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_with_icon, null);

        ImageView icon = layout.findViewById(R.id.toastIcon); // already set in XML
        TextView text = layout.findViewById(R.id.toastText);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
