package com.example.asm_adr;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateBudgetOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget_option);

        // Find buttons by ID
        Button btnSchool = findViewById(R.id.btnschool);
        Button btnTravel = findViewById(R.id.btntravel);
        Button btnShopping = findViewById(R.id.btnshopping);
        Button btnInvest = findViewById(R.id.btninvest);
        ImageView imgBack = findViewById(R.id.imgBack);

        // Set click listeners for buttons
        btnSchool.setOnClickListener(view -> openCreateBudgetActivity("School"));
        btnTravel.setOnClickListener(view -> openCreateBudgetActivity("Travel"));
        btnShopping.setOnClickListener(view -> openCreateBudgetActivity("Shopping"));
        btnInvest.setOnClickListener(view -> openCreateBudgetActivity("Invest"));

        // Back button functionality
        imgBack.setOnClickListener(view -> finish()); // Closes this activity and returns to the previous one
    }

    private void openCreateBudgetActivity(String category) {
        Intent intent = new Intent(CreateBudgetOptionActivity.this, CreateBudgetActivity.class);
        intent.putExtra("CATEGORY", category); // Pass category name
        startActivity(intent);
    }
}
