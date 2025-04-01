package com.example.asm_adr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Budget;

public class EditBudgetActivity extends AppCompatActivity {

    private EditText editCategory, editNote, editAmount, editDate;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private int budgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        // Initialize views
        editCategory = findViewById(R.id.editCategory);
        editNote = findViewById(R.id.editNote);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        btnSave = findViewById(R.id.btnSave);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get budget ID from intent
        Intent intent = getIntent();
        budgetId = intent.getIntExtra("budgetId", -1);
        Budget budget = databaseHelper.getBudgetById(budgetId);

        // Populate fields with existing data
        if (budget != null) {
            editCategory.setText(budget.getCategory());
            editNote.setText(budget.getNote());
            editAmount.setText(String.valueOf(budget.getAmount()));
            editDate.setText(budget.getDate());
        }

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String category = editCategory.getText().toString().trim();
            String note = editNote.getText().toString().trim();
            double amount = Double.parseDouble(editAmount.getText().toString().trim());
            String date = editDate.getText().toString().trim();

            // Update budget in the database
            boolean isUpdated = databaseHelper.updateBudget(new Budget(budgetId, category, note, amount, date));

            if (isUpdated) {
                Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show();
                finish();  // Close the activity and return
            } else {
                Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
