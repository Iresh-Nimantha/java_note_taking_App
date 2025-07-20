package com.example.note_taking_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
                showIconToast("Enter all fields");
            } else if (!pass.equals(confirm)) {
                showIconToast("Passwords do not match!");
            } else if (dbHelper.registerUser(user, pass)) {
                showIconToast("Registration successful!");
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            } else {
                showIconToast("Username exists. Try another.");
            }
        });

        btnToLogin.setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class))
        );
    }

    // Custom NLOGO icon toast
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
