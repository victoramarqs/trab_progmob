package com.example.trabprogmob;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private UserController userController;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userController = new UserController();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        userList = new ArrayList<>();

        // Define adapter
        userAdapter = new UserAdapter(userList, this, new UserAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                User user = userList.get(position);
                userController.deleteUser(user, task -> {
                    if (task.isSuccessful()) {
                        userList.remove(position);
                        userAdapter.notifyItemRemoved(position);
                        Toast.makeText(AdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onRoleChange(int position) {
                User user = userList.get(position);
                String newRole = user.getRole().equals("admin") ? "user" : "admin";
                user.setRole(newRole);
                userController.changeUserRole(user, newRole, task -> {
                    if (task.isSuccessful()) {
                        userAdapter.notifyItemChanged(position);
                        Toast.makeText(AdminActivity.this, "Role changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Revert role change if unsuccessful
                        user.setRole(user.getRole().equals("admin") ? "user" : "admin");
                        userAdapter.notifyItemChanged(position);
                        Toast.makeText(AdminActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        userController.getAllUsers(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userId = document.getId(); // Obtém o ID do documento
                    User user = document.toObject(User.class);
                    user.setId(userId); // Define o ID do usuário como o ID do documento
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(AdminActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
