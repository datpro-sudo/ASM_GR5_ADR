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

import com.example.asm_adr.CreateBudgetOption;
import com.example.asm_adr.R;

public class HomeFragment extends Fragment {
    private ViewFlipper bannerFlipper;
    private ImageView imgBudget1;
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

        // Xử lý sự kiện khi nhấn vào "Budget"
        imgBudget1 = view.findViewById(R.id.imgBudget);
        imgBudget1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetOption.class);
            startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn vào "Banner"
        banner = view.findViewById(R.id.banner);
        banner.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetOption.class);
            startActivity(intent);
        });

        return view;
    }
}
