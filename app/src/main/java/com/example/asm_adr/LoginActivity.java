package com.example.asm_adr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm_adr.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    TextView registerText;
    EditText etEmail, etPassword;
    Button btnLogin;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo UI
        registerText = findViewById(R.id.tv_register);
        etEmail = findViewById(R.id.emailText);
        etPassword = findViewById(R.id.passwordText);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Tự động đăng nhập nếu email đã lưu
        if (sharedPreferences.contains(KEY_EMAIL)) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            navigateToHome(savedEmail);
        }

        // Chuyển đến màn hình đăng ký
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.checkUser(email, password)) { // Kiểm tra trực tiếp không mã hóa
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    saveLoginEmail(email);
                    navigateToHome(email);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLoginEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    private void navigateToHome(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
