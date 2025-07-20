package com.example.note_taking_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private NotesDatabaseHelper dbHelper;
    private EditText editUsername, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dbHelper = new NotesDatabaseHelper(this);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnToRegister = findViewById(R.id.btnToRegister);

        btnLogin.setOnClickListener(v -> {
            String user = editUsername.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.loginUser(user, pass)) {
                Toast.makeText(this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });

        btnToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }
}
