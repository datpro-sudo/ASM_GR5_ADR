package com.example.asm_adr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asm_adr.models.Expense;
import com.example.asm_adr.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2; // Increased version to update database

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_BIRTHDAY = "birthday";
    private static final String COLUMN_SEX = "sex";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " TEXT, " +
            COLUMN_FULLNAME + " TEXT, " +
            COLUMN_BIRTHDAY + " TEXT, " +
            COLUMN_SEX + " TEXT);";

    // Expense Table
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COLUMN_EXPENSE_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "date";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + " (" +
            COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_NOTE + " TEXT, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_DATE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // Upgrade logic for existing users
            db.execSQL(CREATE_TABLE_EXPENSES);
        }
    }

    // ✅ Insert Expense
    public boolean insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_DATE, expense.getDate());

        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();

        return result != -1;
    }

    // ✅ Get All Expenses
    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(0),   // id
                        cursor.getString(1),// category
                        cursor.getString(2),// note
                        cursor.getDouble(3),// amount
                        cursor.getString(4) // date
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }

    // ✅ Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Insert User with Hashed Password
    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String hashedPassword = hashPassword(user.getPassword());

        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_FULLNAME, user.getFullName());
        values.put(COLUMN_BIRTHDAY, user.getBirthday());
        values.put(COLUMN_SEX, user.getSex());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // ✅ Check if Email Exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // ✅ Check User Login
    public boolean checkUser(String email, String enteredPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE email=?", new String[]{email});

        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(0);
            cursor.close();
            return storedHash.equals(hashPassword(enteredPassword));
        }

        cursor.close();
        return false;
    }
}
