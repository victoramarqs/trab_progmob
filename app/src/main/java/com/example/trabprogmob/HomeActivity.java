package com.example.trabprogmob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<ChecklistItem> checklist;
    private RecyclerView checklistRecyclerView;
    private ChecklistAdapter checklistAdapter;
    private EditText itemInput;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checklist = new ArrayList<>();
        checklistRecyclerView = findViewById(R.id.checklistRecyclerView);
        itemInput = findViewById(R.id.itemInput);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemText = itemInput.getText().toString().trim();
                if (!TextUtils.isEmpty(itemText)) {
                    addItemToChecklist(itemText);
                    itemInput.setText("");
                }
            }
        });

        checklistAdapter = new ChecklistAdapter(checklist, new ChecklistAdapter.OnItemCheckedListener() {
            @Override
            public void onItemChecked(int position) {
                removeItemFromChecklist(position);
            }
        });

        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checklistRecyclerView.setAdapter(checklistAdapter);
    }

    private void addItemToChecklist(String itemText) {
        ChecklistItem item = new ChecklistItem(itemText, false);
        checklist.add(item);
        checklistAdapter.notifyItemInserted(checklist.size() - 1);
    }

    private void removeItemFromChecklist(int position) {
        checklist.remove(position);
        checklistAdapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
