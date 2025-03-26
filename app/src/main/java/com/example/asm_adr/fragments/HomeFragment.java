package com.example.asm_adr.fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_adr.R;
import com.example.asm_adr.adapters.ExpenseAdapter;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper databaseHelper;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        // Load expenses from database
        loadExpenses();

        return view;
    }

    private void loadExpenses() {
        List<Expense> expenseList = databaseHelper.getAllExpenses();
        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(adapter);
    }
}

