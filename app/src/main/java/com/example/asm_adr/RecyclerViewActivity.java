package com.example.asm_adr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Data
        itemList = new ArrayList<>();
        itemList.add(new ItemModel("Item 1", R.drawable.fortune)); // Replace with actual drawable images
        itemList.add(new ItemModel("Item 2", R.drawable.fortune));
        itemList.add(new ItemModel("Item 3", R.drawable.fortune));
        itemList.add(new ItemModel("Item 1", R.drawable.fortune)); // Replace with actual drawable images
        itemList.add(new ItemModel("Item 2", R.drawable.fortune));
        itemList.add(new ItemModel("Item 3", R.drawable.fortune));

        itemList.add(new ItemModel("Item 1", R.drawable.fortune)); // Replace with actual drawable images
        itemList.add(new ItemModel("Item 2", R.drawable.fortune));
        itemList.add(new ItemModel("Item 3", R.drawable.fortune));

        itemList.add(new ItemModel("Item 1", R.drawable.fortune)); // Replace with actual drawable images
        itemList.add(new ItemModel("Item 2", R.drawable.fortune));
        itemList.add(new ItemModel("Item 3", R.drawable.fortune));

        adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }
}