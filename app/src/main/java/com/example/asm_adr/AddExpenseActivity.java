package com.example.asm_adr;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText noteEditText, amountEditText, dateEditText;
    private Button saveButton;
    private ImageView backButton;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;
    private String userEmail;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_expense);

        // Initialize views
        categorySpinner = findViewById(R.id.spinnerCategory);
        noteEditText = findViewById(R.id.etNote);
        amountEditText = findViewById(R.id.etMoney);
        dateEditText = findViewById(R.id.etDateTime);
        saveButton = findViewById(R.id.btnSave);
        backButton = findViewById(R.id.imgBack);

        databaseHelper = new DatabaseHelper(this);

        // Lấy userEmail từ Intent hoặc SharedPreferences
        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("loggedInEmail", null);
        }

        if (userEmail == null) {
            Toast.makeText(this, "Please log in to add an expense", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setupCategorySpinner();
        dateEditText.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveExpense());

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCategorySpinner();
    }

    private void setupCategorySpinner() {
        List<String> categories = databaseHelper.getAllCategories(userEmail);
        if (categories.isEmpty()) {
            categories.add("Other");
        }

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = categories.isEmpty() ? "Other" : categories.get(0);
            }
        });
    }

    private void refreshCategorySpinner() {
        List<String> categories = databaseHelper.getAllCategories(userEmail);
        if (categories.isEmpty()) {
            categories.add("Other");
        }

        categoryAdapter.clear();
        categoryAdapter.addAll(categories);
        categoryAdapter.notifyDataSetChanged();

        // Đặt lại giá trị mặc định nếu selectedCategory không còn trong danh sách
        if (selectedCategory == null || !categories.contains(selectedCategory)) {
            selectedCategory = categories.get(0);
            categorySpinner.setSelection(0);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month1 + 1, year1);
                    dateEditText.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveExpense() {
        String note = noteEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (selectedCategory == null || selectedCategory.isEmpty() || note.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
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

        Expense expense = new Expense(selectedCategory, note, amount, date, userEmail);
        boolean success = databaseHelper.insertExpense(expense);

        if (success) {
            Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            refreshCategorySpinner(); // Làm mới Spinner ngay sau khi lưu
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        noteEditText.setText("");
        amountEditText.setText("");
        dateEditText.setText("");
        categorySpinner.setSelection(0);
    }
}