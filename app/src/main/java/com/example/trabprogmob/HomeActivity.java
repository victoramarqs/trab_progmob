package com.example.trabprogmob;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText roleField;
    private Button changeRoleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        roleField = findViewById(R.id.role_field);
        changeRoleButton = findViewById(R.id.change_role_button);

        changeRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRole = roleField.getText().toString();
                updateUserRole(newRole);
            }
        });

        checkUserRole();
    }

    private void checkUserRole() {
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            db.collection("users").document(userId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String role = document.getString("role");
                                    if (!"admin".equals(role)) {
                                        changeRoleButton.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void updateUserRole(String newRole) {
        String userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            db.collection("users").document(userId)
                    .update("role", newRole).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Role updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Exception e = task.getException();
                                Toast.makeText(HomeActivity.this, "Failed to update role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
