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
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Budget;

import java.util.Calendar;

public class CreateBudgetActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText noteEditText, amountEditText, dateEditText;
    private Button saveButton;
    private ImageView backButton;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);

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
            userEmail = prefs.getString("userEmail", null);
        }

        // Kiểm tra nếu userEmail là null (chưa đăng nhập)
        if (userEmail == null) {
            Toast.makeText(this, "Please log in to add a budget", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Get category from Intent
        selectedCategory = getIntent().getStringExtra("CATEGORY");

        // Log the received category for debugging
        Log.d("CreateBudgetActivity", "Received category: " + selectedCategory);

        setupCategorySpinner();
        dateEditText.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveBudget());

        // Handle back button click
        backButton.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        String[] categories = {"School", "Travel", "Shopping", "Invest", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set spinner selection based on received category
        if (selectedCategory != null) {
            int position = getCategoryPosition(categories, selectedCategory);
            categorySpinner.setSelection(position);
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Other";
            }
        });
    }

    // Get the position of the selected category in the spinner array
    private int getCategoryPosition(String[] categories, String category) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(category)) {
                return i;
            }
        }
        return 4; // Default to "Other" if category is not found
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

    private void saveBudget() {
        String note = noteEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (selectedCategory.isEmpty() || note.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
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

        Budget budget = new Budget(selectedCategory, note, amount, date, userEmail);
        boolean success = databaseHelper.insertBudget(budget);

        if (success) {
            Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        noteEditText.setText("");
        amountEditText.setText("");
        dateEditText.setText("");
        categorySpinner.setSelection(0);
    }
}