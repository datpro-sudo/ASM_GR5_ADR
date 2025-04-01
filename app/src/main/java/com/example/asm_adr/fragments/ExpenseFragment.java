package com.example.asm_adr.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_adr.AddExpenseActivity;
import com.example.asm_adr.LoginActivity;
import com.example.asm_adr.R;
import com.example.asm_adr.adapters.ExpenseAdapter;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class ExpenseFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper databaseHelper;
    private ImageView imgAddExpense;
    private String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_expense, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        // Lấy email người dùng từ SharedPreferences với key đồng bộ
        refreshUserEmail();
        if (userEmail == null) {
            Toast.makeText(getContext(), "Please log in to view expenses", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return view;
        }

        loadExpenses();

        // Find the "Add Expense" button
        imgAddExpense = view.findViewById(R.id.imgAddExpense);
        imgAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
            intent.putExtra("userEmail", userEmail); // Truyền userEmail để sử dụng trong AddExpenseActivity
            startActivity(intent);
        });

        return view;
    }

    private void refreshUserEmail() {
        // Lấy userEmail từ arguments (nếu có) hoặc SharedPreferences
        if (getArguments() != null && getArguments().containsKey("userEmail")) {
            userEmail = getArguments().getString("userEmail");
        } else {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
            userEmail = prefs.getString("userEmail", null); // Đồng bộ với LoginActivity
        }
    }

    private void loadExpenses() {
        List<Expense> expenseList = databaseHelper.getAllExpenses(userEmail);
        adapter = new ExpenseAdapter(expenseList, databaseHelper, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserEmail();
        if (userEmail != null) {
            loadExpenses(); // Reload expenses khi quay lại fragment
        } else {
            Toast.makeText(getContext(), "Please log in to view expenses", Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(null);
            redirectToLogin();
        }
    }
}