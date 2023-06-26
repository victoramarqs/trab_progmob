package com.example.trabprogmob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {
    private List<ChecklistItem> checklistItems;
    private OnItemCheckedListener onItemCheckedListener;

    public interface OnItemCheckedListener {
        void onItemChecked(int position);
    }

    public ChecklistAdapter(List<ChecklistItem> checklistItems, OnItemCheckedListener listener) {
        this.checklistItems = checklistItems;
        this.onItemCheckedListener = listener;
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_item, parent, false);
        return new ChecklistViewHolder(view, onItemCheckedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder holder, int position) {
        ChecklistItem item = checklistItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    class ChecklistViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        ChecklistViewHolder(@NonNull View itemView, OnItemCheckedListener listener) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    listener.onItemChecked(getAdapterPosition());
                }
            });
        }

        void bind(final ChecklistItem item) {
            checkBox.setText(item.getText());
            checkBox.setChecked(item.isChecked());
        }
    }
}
