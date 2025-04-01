package com.example.asm_adr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asm_adr.models.Expense;
import com.example.asm_adr.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 3; // Tăng version để áp dụng thay đổi

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
    private static final String COLUMN_USER_EMAIL = "userEmail"; // Khóa phụ

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + " (" +
            COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_NOTE + " TEXT, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_USER_EMAIL + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "));";

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
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL(CREATE_TABLE_EXPENSES);
        }
        if (oldVersion < 3) {
            // Thêm cột userEmail vào bảng expenses nếu chưa có
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_USER_EMAIL + " TEXT");
            db.execSQL("CREATE TABLE temp_expenses AS SELECT * FROM " + TABLE_EXPENSES);
            db.execSQL("DROP TABLE " + TABLE_EXPENSES);
            db.execSQL(CREATE_TABLE_EXPENSES);
            db.execSQL("INSERT INTO " + TABLE_EXPENSES + " SELECT * FROM temp_expenses");
            db.execSQL("DROP TABLE temp_expenses");
        }
    }

    // Insert Expense
    public boolean insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_DATE, expense.getDate());
        values.put(COLUMN_USER_EMAIL, expense.getUserEmail()); // Thêm userEmail

        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();

        return result != -1;
    }

    // Get All Expenses for a specific user
    public List<Expense> getAllExpenses(String userEmail) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Nếu userEmail là null, trả về danh sách rỗng hoặc xử lý theo yêu cầu
        if (userEmail == null) {
            db.close();
            return expenseList; // Trả về danh sách rỗng
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USER_EMAIL + "=?", new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(0),    // id
                        cursor.getString(1), // category
                        cursor.getString(2), // note
                        cursor.getDouble(3), // amount
                        cursor.getString(4), // date
                        cursor.getString(5)  // userEmail
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }

    // Insert User (without password hashing)
    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_FULLNAME, user.getFullName());
        values.put(COLUMN_BIRTHDAY, user.getBirthday());
        values.put(COLUMN_SEX, user.getSex());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Delete Expense
    public boolean deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_EXPENSES, COLUMN_EXPENSE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    // Get Expense by ID
    public Expense getExpenseById(int expenseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, null, COLUMN_EXPENSE_ID + " = ?", new String[]{String.valueOf(expenseId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_EXPENSE_ID);
            int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
            int noteIndex = cursor.getColumnIndex(COLUMN_NOTE);
            int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int userEmailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);

            if (idIndex == -1 || categoryIndex == -1 || noteIndex == -1 || amountIndex == -1 || dateIndex == -1 || userEmailIndex == -1) {
                cursor.close();
                return null;
            }

            int id = cursor.getInt(idIndex);
            String category = cursor.getString(categoryIndex);
            String note = cursor.getString(noteIndex);
            double amount = cursor.getDouble(amountIndex);
            String date = cursor.getString(dateIndex);
            String userEmail = cursor.getString(userEmailIndex);

            cursor.close();
            return new Expense(id, category, note, amount, date, userEmail);
        }
        cursor.close();
        return null;
    }

    // Update Expense
    public boolean updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_DATE, expense.getDate());
        values.put(COLUMN_USER_EMAIL, expense.getUserEmail()); // Thêm userEmail

        int rowsAffected = db.update(TABLE_EXPENSES, values, COLUMN_EXPENSE_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
        return rowsAffected > 0;
    }

    // Check if Email Exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // Check User Login (without password hashing)
    public boolean checkUser(String email, String enteredPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM " + TABLE_USERS + " WHERE email=?", new String[]{email});

        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            cursor.close();
            db.close();
            return storedPassword.equals(enteredPassword);
        }

        cursor.close();
        db.close();
        return false;
    }
}