package com.example.asm_adr.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asm_adr.LoginActivity;
import com.example.asm_adr.R;
import com.example.asm_adr.database.DatabaseHelper;

public class ProfileFragment extends Fragment {

    private TextView tvEmail, tvFullName, tvBirthday, tvSex;
    private EditText etFullName, etBirthday, etSex, etNewPassword;
    private Button btnBack, btnUpdateProfile;
    private DatabaseHelper databaseHelper;
    private String userEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        databaseHelper = new DatabaseHelper(getActivity());

        // Lấy email từ SharedPreferences với key đồng bộ
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", null); // Đồng bộ với LoginActivity

        if (userEmail != null && !userEmail.isEmpty()) {
            loadUserProfile(userEmail);
        } else {
            // Nếu không có userEmail, chuyển về LoginActivity ngay lập tức
            Toast.makeText(getActivity(), "Please log in to view profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }

        // Xử lý đăng xuất
        btnBack.setOnClickListener(v -> logout());

        // Cập nhật thông tin người dùng
        btnUpdateProfile.setOnClickListener(v -> updateUserProfile());
    }

    private void initializeViews(View view) {
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        tvSex = view.findViewById(R.id.tvSex);

        etFullName = view.findViewById(R.id.etFullName);
        etBirthday = view.findViewById(R.id.etBirthday);
        etSex = view.findViewById(R.id.etSex);
        etNewPassword = view.findViewById(R.id.etNewPassword);

        btnBack = view.findViewById(R.id.btn_Back);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
    }

    private void loadUserProfile(String email) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT fullname, birthday, sex FROM users WHERE email=?", new String[]{email});

        if (cursor.moveToFirst()) {
            tvEmail.setText(email);
            tvFullName.setText(cursor.getString(0)); // fullname
            tvBirthday.setText(cursor.getString(1)); // birthday
            tvSex.setText(cursor.getString(2)); // sex
        } else {
            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private void updateUserProfile() {
        String newFullName = etFullName.getText().toString().trim();
        String newBirthday = etBirthday.getText().toString().trim();
        String newSex = etSex.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (newFullName.isEmpty() && newBirthday.isEmpty() && newSex.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter at least one field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!newFullName.isEmpty()) values.put("fullname", newFullName);
        if (!newBirthday.isEmpty()) values.put("birthday", newBirthday);
        if (!newSex.isEmpty()) values.put("sex", newSex);
        if (!newPassword.isEmpty()) values.put("password", newPassword);

        int rowsUpdated = db.update("users", values, "email=?", new String[]{userEmail});
        db.close();

        if (rowsUpdated > 0) {
            Toast.makeText(getActivity(), "Profile updated successfully! Please log in again.", Toast.LENGTH_SHORT).show();
            loadUserProfile(userEmail); // Reload updated profile data trước khi đăng xuất

            // Xóa thông tin đăng nhập và chuyển về LoginActivity
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply(); // Xóa dữ liệu trong SharedPreferences

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish(); // Đóng activity hiện tại
        } else {
            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // Xóa toàn bộ dữ liệu trong SharedPreferences "UserPrefs"
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Chuyển về LoginActivity và xóa stack
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}