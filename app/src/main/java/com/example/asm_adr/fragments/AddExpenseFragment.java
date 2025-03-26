package com.example.asm_adr.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.asm_adr.R;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.Calendar;

public class AddExpenseFragment extends Fragment {

    private Spinner categorySpinner;
    private EditText noteEditText, amountEditText, dateEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;

    private String selectedCategory;

    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Initialize views
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        noteEditText = view.findViewById(R.id.etNote);
        amountEditText = view.findViewById(R.id.etMoney);
        dateEditText = view.findViewById(R.id.etDateTime);
        saveButton = view.findViewById(R.id.btnSave);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Set up category spinner
        setupCategorySpinner();

        // Set up date picker dialog
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Save button click listener
        saveButton.setOnClickListener(v -> saveExpense());

        return view;
    }

    private void setupCategorySpinner() {
        // Sample categories
        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Entertainment", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Other"; // Default category
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Create expense object
        Expense expense = new Expense(selectedCategory, note, amount, date);

        // Insert into database
        boolean success = databaseHelper.insertExpense(expense);

        if (success) {
            Toast.makeText(getContext(), "Expense saved successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getContext(), "Error saving expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        noteEditText.setText("");
        amountEditText.setText("");
        dateEditText.setText("");
        categorySpinner.setSelection(0);
    }
}
