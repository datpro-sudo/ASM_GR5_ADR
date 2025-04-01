package com.example.asm_adr;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private String userEmail; // Thêm biến để lưu email người dùng

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

        // Lấy email người dùng từ SharedPreferences (giả định đã lưu khi đăng nhập)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInEmail", null);
        if (userEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Thoát nếu không có người dùng đăng nhập
            return;
        }

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
            // userEmail được lấy từ expense để đảm bảo không thay đổi
            userEmail = expense.getUserEmail();
        } else {
            Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Handle Save button click
        btnSave.setOnClickListener(v -> saveUpdatedExpense());
    }

    private void saveUpdatedExpense() {
        String category = editCategory.getText().toString().trim();
        String note = editNote.getText().toString().trim();
        String amountStr = editAmount.getText().toString().trim();
        String date = editDate.getText().toString().trim();

        // Validate input
        if (category.isEmpty() || note.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create updated expense object with userEmail
        Expense updatedExpense = new Expense(expenseId, category, note, amount, date, userEmail);

        // Update expense in the database
        boolean isUpdated = databaseHelper.updateExpense(updatedExpense);

        if (isUpdated) {
            Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and go back
        } else {
            Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
        }
    }
}