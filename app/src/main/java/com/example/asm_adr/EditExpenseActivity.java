package com.example.asm_adr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText editCategory, editNote, editAmount, editDate;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Initialize views
        editCategory = findViewById(R.id.editCategory);
        editNote = findViewById(R.id.editNote);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        btnSave = findViewById(R.id.btnSave);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get expense ID from intent
        Intent intent = getIntent();
        expenseId = intent.getIntExtra("expenseId", -1);
        Expense expense = databaseHelper.getExpenseById(expenseId);

        // Populate fields with existing data
        if (expense != null) {
            editCategory.setText(expense.getCategory());
            editNote.setText(expense.getNote());
            editAmount.setText(String.valueOf(expense.getAmount()));
            editDate.setText(expense.getDate());
        }

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            String category = editCategory.getText().toString().trim();
            String note = editNote.getText().toString().trim();
            double amount = Double.parseDouble(editAmount.getText().toString().trim());
            String date = editDate.getText().toString().trim();

            // Update expense in the database
            boolean isUpdated = databaseHelper.updateExpense(new Expense(expenseId, category, note, amount, date));

            if (isUpdated) {
                Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                finish();  // Close the activity and go back
            } else {
                Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
