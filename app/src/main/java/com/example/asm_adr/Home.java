package com.example.asm_adr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.asm_adr.fragments.HomeFragment;

public class Home extends AppCompatActivity {

    private ViewFlipper bannerFlipper;
    private ImageView  imgBudget1;
    private BottomNavigationView bottomNavigationView; // Thêm biến cho Navigation View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo ViewFlipper
        bannerFlipper = findViewById(R.id.viewFlipper);
        bannerFlipper.startFlipping();

        // Xử lý sự kiện cho các ImageView
        imgBudget1 = findViewById(R.id.imgBudget);
        imgBudget1.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CreateBudgetOption.class);
            startActivity(intent);
        });

        RelativeLayout banner = findViewById(R.id.banner);
        banner.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CreateBudgetOption.class);
            startActivity(intent);
        });


    }
}
