package com.example.note_taking_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private NotesDatabaseHelper dbHelper;
    private EditText editUsername, editPassword, editConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new NotesDatabaseHelper(this);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editConfirm = findViewById(R.id.editConfirm);

        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnToLogin = findViewById(R.id.btnToLogin);

        btnRegister.setOnClickListener(v -> {
            String user = editUsername.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();
            String confirm = editConfirm.getText().toString().trim();
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.registerUser(user, pass)) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Username exists. Try another.", Toast.LENGTH_SHORT).show();
            }
        });

        btnToLogin.setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class))
        );
    }
}
