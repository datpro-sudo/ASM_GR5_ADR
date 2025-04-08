package com.example.asm_adr.fragments;

import android.content.Intent;
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

import com.example.asm_adr.LoginActivity;
import com.example.asm_adr.R;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class CreateCategoryFragment extends Fragment {

    private EditText categoryEditText;
    private Button saveButton, deleteButton;
    private Spinner categorySpinner;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;

    public CreateCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_category, container, false);

        // Khởi tạo các thành phần giao diện
        categoryEditText = view.findViewById(R.id.categoryEditText);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        databaseHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng
        String userEmail = getUserEmail();
        if (userEmail == null) {
            Toast.makeText(getContext(), "User not logged in. Redirecting to login...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            return view;
        }

        // Thiết lập Spinner với danh sách category
        setupCategorySpinner(userEmail);

        // Xử lý sự kiện khi nhấn nút Save
        saveButton.setOnClickListener(v -> {
            String categoryName = categoryEditText.getText().toString().trim();
            if (categoryName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Expense với category mới
            Expense expense = new Expense(categoryName, "Default Note", 0.0, "2025-04-03", userEmail);
            boolean isInserted = databaseHelper.insertExpense(expense);
            if (isInserted) {
                Toast.makeText(getContext(), "Category '" + categoryName + "' added successfully", Toast.LENGTH_SHORT).show();
                categoryEditText.setText(""); // Xóa trường nhập sau khi lưu
                setupCategorySpinner(userEmail); // Làm mới Spinner
            } else {
                Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi nhấn nút Delete
        deleteButton.setOnClickListener(v -> {
            if (selectedCategory == null || selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "Please select a category to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xóa tất cả các Expense có category đã chọn
            boolean isDeleted = databaseHelper.deleteExpensesByCategory(selectedCategory, userEmail);
            if (isDeleted) {
                Toast.makeText(getContext(), "Category '" + selectedCategory + "' deleted successfully", Toast.LENGTH_SHORT).show();
                setupCategorySpinner(userEmail); // Làm mới Spinner sau khi xóa
            } else {
                Toast.makeText(getContext(), "Failed to delete category or no matching expenses found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Thiết lập Spinner với danh sách category từ database
    private void setupCategorySpinner(String userEmail) {
        List<String> categories = databaseHelper.getAllCategories(userEmail);
        if (categories.isEmpty()) {
            categories.add("No categories available");
            deleteButton.setEnabled(false); // Vô hiệu hóa nút Delete nếu không có category
        } else {
            deleteButton.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
                if (selectedCategory.equals("No categories available")) {
                    selectedCategory = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });
    }

    // Lấy email người dùng từ SharedPreferences
    private String getUserEmail() {
        return requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE)
                .getString("userEmail", null);
    }
}