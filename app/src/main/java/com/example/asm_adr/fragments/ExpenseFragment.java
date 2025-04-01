package com.example.asm_adr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_adr.R;
import com.example.asm_adr.AddExpenseActivity;
import com.example.asm_adr.adapters.ExpenseAdapter;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class ExpenseFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper databaseHelper;
    private ImageView imgAddExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_expense, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        // Load expenses from the database
        loadExpenses();

        // Find the "Add Expense" button
        imgAddExpense = view.findViewById(R.id.imgAddExpense);
        imgAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadExpenses() {
        List<Expense> expenseList = databaseHelper.getAllExpenses();
        adapter = new ExpenseAdapter(expenseList, databaseHelper, getContext());  // Pass context here
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadExpenses(); // Reload expenses when returning to the fragment
    }
}
