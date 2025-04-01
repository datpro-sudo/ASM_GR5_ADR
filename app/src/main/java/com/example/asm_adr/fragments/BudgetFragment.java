package com.example.asm_adr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.asm_adr.LoginActivity;
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
    private String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_budget, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());

        // Lấy userEmail
        refreshUserEmail();
        if (userEmail == null) {
            Toast.makeText(getContext(), "Please log in to view budgets", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return view;
        }

        loadBudgets();

        imgAddBudget = view.findViewById(R.id.imgAddBudget);
        imgAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        return view;
    }

    private void refreshUserEmail() {
        // Ưu tiên lấy từ arguments, sau đó từ SharedPreferences
        if (getArguments() != null && getArguments().containsKey("userEmail")) {
            userEmail = getArguments().getString("userEmail");
        } else {
            userEmail = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE)
                    .getString("userEmail", null);
        }
    }

    private void loadBudgets() {
        List<Budget> budgetList = databaseHelper.getAllBudgets(userEmail);
        adapter = new BudgetAdapter(budgetList, getContext());
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
            loadBudgets();
        } else {
            Toast.makeText(getContext(), "Please log in to view budgets", Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(null);
            redirectToLogin();
        }
    }
}