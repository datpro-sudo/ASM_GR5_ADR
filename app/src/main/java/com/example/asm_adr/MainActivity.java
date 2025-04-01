package com.example.asm_adr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import androidx.fragment.app.Fragment;

import com.example.asm_adr.fragments.AddExpenseFragment;
import com.example.asm_adr.fragments.NotificationFragment;
import com.example.asm_adr.fragments.ProfileFragment;
import com.example.asm_adr.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())  // Hiển thị HomeFragment mặc định
                    .commit();
        }



        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_notification) {
                selectedFragment = new NotificationFragment();

            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
