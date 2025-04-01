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

        // Lấy email người dùng từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
        userEmail = prefs.getString("loggedInEmail", null);

        // Load expenses nếu userEmail không null
        if (userEmail != null) {
            loadExpenses();
        } else {
            Toast.makeText(getContext(), "Please log in to view expenses", Toast.LENGTH_SHORT).show();
        }

        // Find the "Add Expense" button
        imgAddExpense = view.findViewById(R.id.imgAddExpense);
        imgAddExpense.setOnClickListener(v -> {
            if (userEmail != null) {
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Please log in to add expenses", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadExpenses() {
        List<Expense> expenseList = databaseHelper.getAllExpenses(userEmail);
        adapter = new ExpenseAdapter(expenseList, databaseHelper, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userEmail != null) {
            loadExpenses(); // Reload expenses khi quay lại fragment
        }
    }
}