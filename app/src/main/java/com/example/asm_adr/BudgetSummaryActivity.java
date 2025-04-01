package com.example.asm_adr;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BudgetSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_summary);

        TextView tvSummary = findViewById(R.id.tvSummary);

        // Retrieve passed data
        String category = getIntent().getStringExtra("CATEGORY");
        String note = getIntent().getStringExtra("NOTE");
        String amount = getIntent().getStringExtra("AMOUNT");
        String date = getIntent().getStringExtra("DATE");

        // Display budget summary
        String summary = "Category: " + category + "\nNote: " + note + "\nAmount: " + amount + "\nDate: " + date;
        tvSummary.setText(summary);
    }
}
