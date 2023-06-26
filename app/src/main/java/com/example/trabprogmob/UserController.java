package com.example.trabprogmob;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UserController() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    public void register(String email, String password, OnCompleteListener<AuthResult> callback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Map<String, Object> userDoc = new HashMap<>();
                userDoc.put("role", "user");

                db.collection("users").document(user.getUid()).set(userDoc).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        callback.onComplete(task);
                    } else {
                        callback.onComplete(null);
                    }
                });
            } else {
                callback.onComplete(task);
            }
        });
    }


    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }
}


