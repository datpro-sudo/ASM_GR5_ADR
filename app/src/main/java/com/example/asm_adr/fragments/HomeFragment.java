package com.example.asm_adr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.asm_adr.AddExpenseActivity;
import com.example.asm_adr.CreateBudgetOptionActivity;
import com.example.asm_adr.R;

public class HomeFragment extends Fragment {
    private ViewFlipper bannerFlipper;
    private ImageView imgBudget1;
    private ImageView imgExpense1;
    private RelativeLayout banner;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup ViewFlipper
        bannerFlipper = view.findViewById(R.id.viewFlipper);
        bannerFlipper.startFlipping();

        // Handle "Budget" icon click
        imgBudget1 = view.findViewById(R.id.imgBudget);
        imgBudget1.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new BudgetFragment()); // Ensure fragment_container exists
            transaction.addToBackStack(null); // Allows going back to the previous fragment
            transaction.commit();
        });

        // Handle "Expense" click - Navigate to ExpenseFragment
        imgExpense1 = view.findViewById(R.id.imgExpense);
        imgExpense1.setOnClickListener(v -> openExpenseFragment());

        // Handle "Banner" click
        banner = view.findViewById(R.id.banner);
        banner.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new BudgetFragment()); // Ensure fragment_container is your FrameLayout ID
            transaction.addToBackStack(null);
            transaction.commit();
        });


        banner = view.findViewById(R.id.banner2);
        banner.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(getActivity(), AddExpenseActivity.class));
        });

        return view;
    }

    private void openExpenseFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace HomeFragment with ExpenseFragment
        transaction.replace(R.id.fragment_container, new ExpenseFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
