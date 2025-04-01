package com.example.asm_adr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.User;
import com.example.asm_adr.PasswordUtils;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etFullName, etBirthday;
    private RadioGroup rgSex;
    private Button btnRegister;
    private TextView registerText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Initialize Views
        etFullName = findViewById(R.id.fullNameText);
        etBirthday = findViewById(R.id.birthdayText);
        rgSex = findViewById(R.id.rgSex);
        etEmail = findViewById(R.id.emailText);
        etPassword = findViewById(R.id.passwordText);
        btnRegister = findViewById(R.id.btnRegister);
        registerText = findViewById(R.id.tv_login);

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());

        // Navigate to Login Activity
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        int selectedId = rgSex.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String sex = (selectedRadioButton != null) ? selectedRadioButton.getText().toString() : "";

        // Validation
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || birthday.isEmpty() || sex.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash password before saving

        // Save to Database
        User user = new User(email, password, fullName, birthday, sex);
        boolean isInserted = dbHelper.insertUser(user);

        if (isInserted) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration Failed. Try Again!", Toast.LENGTH_SHORT).show();
        }
    }

    // Kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }
}
