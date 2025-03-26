package com.example.asm_adr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<ItemModel> itemList;

    // Constructor to pass the list of items
    public ItemAdapter(List<ItemModel> itemList) {
        this.itemList = itemList;
    }

    // Creates new ViewHolder objects
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Binds data to UI components inside ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = itemList.get(position);
        holder.textView.setText(item.getTitle());  // Set text
        holder.imageView.setImageResource(item.getImageResource()); // Set image
    }

    // Returns the total number of items
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class holds references to UI elements
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
            imageView = itemView.findViewById(R.id.item_image);
        }
    }
}

