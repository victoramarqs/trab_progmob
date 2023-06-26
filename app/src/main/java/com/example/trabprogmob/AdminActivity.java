package com.example.trabprogmob;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private UserController userController;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userController = new UserController();
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        userList = new ArrayList<>();

        // Recuperar o currentUserId do intent
        currentUserId = getIntent().getStringExtra("currentUserId");

        // Define adapter
        userAdapter = new UserAdapter(userList, this, new UserAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                User user = userList.get(position);
                userController.deleteUser(user, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userList.remove(position);
                            userAdapter.notifyItemRemoved(position);
                            Toast.makeText(AdminActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onRoleChange(int position) {
                User user = userList.get(position);
                String newRole = user.getRole().equals("admin") ? "user" : "admin";
                userController.changeUserRole(user, newRole, task -> {
                    if (task.isSuccessful()) {
                        user.setRole(newRole);
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
        userController.getAllUsers(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = document.getId();
                        User user = document.toObject(User.class);
                        user.setId(userId);
                        // Verifica se o usuário é o admin autenticado e o ignora na lista
                        if (!user.getId().equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // Handle logout action
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear any user session or credentials and navigate back to the login screen
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
