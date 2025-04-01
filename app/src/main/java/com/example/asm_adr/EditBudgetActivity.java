package com.example.asm_adr;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private String userEmail;

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

        // Get budget ID and userEmail from intent
        Intent intent = getIntent();
        budgetId = intent.getIntExtra("budgetId", -1);
        userEmail = intent.getStringExtra("userEmail");

        // Nếu userEmail không có từ Intent, lấy từ SharedPreferences
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("userEmail", null);
        }

        // Kiểm tra nếu chưa đăng nhập
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to edit a budget", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        Budget budget = databaseHelper.getBudgetById(budgetId);

        // Populate fields with existing data
        if (budget != null) {
            // Kiểm tra xem budget thuộc về user hiện tại không
            if (!userEmail.equals(budget.getUserEmail())) {
                Toast.makeText(this, "You can only edit your own budgets", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            editCategory.setText(budget.getCategory());
            editNote.setText(budget.getNote());
            editAmount.setText(String.valueOf(budget.getAmount()));
            editDate.setText(budget.getDate());
        } else {
            Toast.makeText(this, "Budget not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Handle Save button click
        btnSave.setOnClickListener(v -> saveUpdatedBudget());
    }

    private void saveUpdatedBudget() {
        String category = editCategory.getText().toString().trim();
        String note = editNote.getText().toString().trim();
        String amountStr = editAmount.getText().toString().trim();
        String date = editDate.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
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

        // Tạo Budget mới với userEmail
        Budget updatedBudget = new Budget(budgetId, category, note, amount, date, userEmail);

        // Cập nhật Budget trong cơ sở dữ liệu
        boolean isUpdated = databaseHelper.updateBudget(updatedBudget);

        if (isUpdated) {
            Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
        }
    }
}