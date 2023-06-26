package com.example.trabprogmob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegisterActivity extends AppCompatActivity {

    private UserController userController;
    private EditText emailField;
    private EditText passwordField;
    private Button registerButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userController = new UserController();
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        registerButton = findViewById(R.id.register_button);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        userController.register(email, password, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                // If register was not successful, display error message
                String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}




