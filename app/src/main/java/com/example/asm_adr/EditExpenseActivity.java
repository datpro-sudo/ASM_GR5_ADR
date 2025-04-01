package com.example.asm_adr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private String userEmail;

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

        // Lấy userEmail từ Intent trước, sau đó từ SharedPreferences
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("userEmail", null); // Đồng bộ key với AddExpenseActivity
        }

        // Kiểm tra nếu chưa đăng nhập
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to edit expense", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }, 1000); // Đợi 1 giây để Toast hiển thị
            return;
        }

        // Get expense ID from intent
        expenseId = intent.getIntExtra("expenseId", -1);
        Expense expense = databaseHelper.getExpenseById(expenseId);

        // Populate fields with existing data
        if (expense == null) {
            Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editCategory.setText(expense.getCategory());
        editNote.setText(expense.getNote());
        editAmount.setText(String.valueOf(expense.getAmount()));
        editDate.setText(expense.getDate());
        // Không cần gán lại userEmail từ expense, giữ nguyên từ Intent/SharedPreferences

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
            finish();
        } else {
            Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
        }
    }
}