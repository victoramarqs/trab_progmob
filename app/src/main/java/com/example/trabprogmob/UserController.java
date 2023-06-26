package com.example.trabprogmob;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserController {
    private FirebaseFirestore db;

    public UserController() {
        db = FirebaseFirestore.getInstance();
    }

    public void register(String userId, String email, String password, OnCompleteListener<Void> callback) {
        // Criptografa a senha usando BCrypt
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User();
        user.setId(userId); // Define o ID do usuário
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole("user");

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(callback);
    }


    public void login(String email, String password, OnCompleteListener<User> callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            User user = snapshot.getDocuments().get(0).toObject(User.class);
                            if (user != null && BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified) {
                                callback.onComplete(Tasks.forResult(user));
                            } else {
                                callback.onComplete(Tasks.forException(new Exception("Invalid credentials")));
                            }
                        } else {
                            callback.onComplete(Tasks.forException(new Exception("User not found")));
                        }
                    } else {
                        callback.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }


    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("users")
                .get()
                .addOnCompleteListener(listener);
    }

    public void deleteUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users").document(user.getId())
                .delete()
                .addOnCompleteListener(listener);
    }

    public void changeUserRole(User user, String newRole, OnCompleteListener<Void> listener) {
        db.collection("users").document(user.getId())
                .update("role", newRole)
                .addOnCompleteListener(listener);
    }
}
