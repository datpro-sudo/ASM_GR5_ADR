package com.example.asm_adr;

import android.app.DatePickerDialog;
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

public class AddExpenseActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText noteEditText, amountEditText, dateEditText;
    private Button saveButton;
    private ImageView backButton;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;
    private String userEmail; // Thêm biến để lưu email người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_expense); // Đảm bảo tên layout đúng

        // Initialize views
        categorySpinner = findViewById(R.id.spinnerCategory);
        noteEditText = findViewById(R.id.etNote);
        amountEditText = findViewById(R.id.etMoney);
        dateEditText = findViewById(R.id.etDateTime);
        saveButton = findViewById(R.id.btnSave);
        backButton = findViewById(R.id.imgBack);

        databaseHelper = new DatabaseHelper(this);

        // Lấy email người dùng từ SharedPreferences (hoặc Intent)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInEmail", null); // Giả định email được lưu khi đăng nhập
        if (userEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Thoát nếu không có người dùng đăng nhập
            return;
        }

        setupCategorySpinner();
        dateEditText.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveExpense());

        // Handle back button click
        backButton.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Entertainment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
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

        // Tạo Expense với userEmail
        Expense expense = new Expense(selectedCategory, note, amount, date, userEmail);

        boolean success = databaseHelper.insertExpense(expense);

        if (success) {
            Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            finish(); // Đóng activity sau khi lưu
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