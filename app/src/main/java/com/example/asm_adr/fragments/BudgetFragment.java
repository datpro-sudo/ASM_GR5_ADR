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
import com.example.asm_adr.CreateBudgetActivity;
import com.example.asm_adr.adapters.BudgetAdapter;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Budget;

import java.util.List;

public class BudgetFragment extends Fragment {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private DatabaseHelper databaseHelper;
    private ImageView imgAddBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_budget, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        loadBudgets();

        imgAddBudget = view.findViewById(R.id.imgAddBudget);
        imgAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadBudgets() {
        List<Budget> budgetList = databaseHelper.getAllBudgets();
        adapter = new BudgetAdapter(budgetList, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgets();
    }
}
