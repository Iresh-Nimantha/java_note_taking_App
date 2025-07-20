package com.example.note_taking_app;

import android.content.Intent;
import android.content.SharedPreferences;
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
                showIconToast("Enter all fields");
            } else if (dbHelper.loginUser(user, pass)) {
                // Save login state so user stays signed in
                SharedPreferences.Editor editor = getSharedPreferences("auth", MODE_PRIVATE).edit();
                editor.putBoolean("signedIn", true);
                editor.apply();

                showIconToast("Sign in successful!");
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                showIconToast("Invalid credentials!");
            }
        });

        btnToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }

    // Custom toast with NLOGO icon
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
