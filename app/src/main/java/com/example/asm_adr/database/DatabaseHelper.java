package com.example.asm_adr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asm_adr.models.Expense;
import com.example.asm_adr.models.User;
import com.example.asm_adr.models.Budget;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4; // Giữ version 4

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

    // Budget Table
    private static final String TABLE_BUDGETS = "budgets";
    private static final String COLUMN_BUDGET_ID = "id";
    private static final String COLUMN_BUDGET_CATEGORY = "category";
    private static final String COLUMN_BUDGET_NOTE = "note";
    private static final String COLUMN_BUDGET_AMOUNT = "amount";
    private static final String COLUMN_BUDGET_DATE = "date";
    private static final String COLUMN_BUDGET_USER_EMAIL = "userEmail"; // Khóa phụ

    private static final String CREATE_TABLE_BUDGETS = "CREATE TABLE " + TABLE_BUDGETS + " (" +
            COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BUDGET_CATEGORY + " TEXT, " +
            COLUMN_BUDGET_NOTE + " TEXT, " +
            COLUMN_BUDGET_AMOUNT + " REAL, " +
            COLUMN_BUDGET_DATE + " TEXT, " +
            COLUMN_BUDGET_USER_EMAIL + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_BUDGET_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_BUDGETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Kiểm tra và xử lý từng bước nâng cấp
        if (oldVersion < 2) {
            // Từ version 1 lên 2: Xóa và tạo lại bảng expenses
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL(CREATE_TABLE_EXPENSES);
        }
        if (oldVersion < 3) {
            // Từ version 2 lên 3: Tạo bảng budgets nếu chưa có
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
            db.execSQL(CREATE_TABLE_BUDGETS);
        }
        if (oldVersion < 4) {
            // Từ version 3 lên 4: Kiểm tra xem bảng budgets tồn tại chưa
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{TABLE_BUDGETS});
            boolean budgetsTableExists = cursor.getCount() > 0;
            cursor.close();

            if (!budgetsTableExists) {
                // Nếu bảng budgets không tồn tại, tạo mới
                db.execSQL(CREATE_TABLE_BUDGETS);
            } else {
                // Nếu bảng budgets đã tồn tại, thêm cột userEmail
                try {
                    db.execSQL("ALTER TABLE " + TABLE_BUDGETS + " ADD COLUMN " + COLUMN_BUDGET_USER_EMAIL + " TEXT");
                } catch (Exception e) {
                    // Nếu thêm cột thất bại (do cột đã tồn tại hoặc lỗi khác), bỏ qua
                }
            }
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
        values.put(COLUMN_USER_EMAIL, expense.getUserEmail());

        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();

        return result != -1;
    }

    // Get All Expenses for a specific user
    public List<Expense> getAllExpenses(String userEmail) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (userEmail == null) {
            db.close();
            return expenseList;
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

    // Insert User
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
        values.put(COLUMN_USER_EMAIL, expense.getUserEmail());

        int rowsAffected = db.update(TABLE_EXPENSES, values, COLUMN_EXPENSE_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
        return rowsAffected > 0;
    }

    // Insert Budget
    public boolean insertBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BUDGET_CATEGORY, budget.getCategory());
        values.put(COLUMN_BUDGET_NOTE, budget.getNote());
        values.put(COLUMN_BUDGET_AMOUNT, budget.getAmount());
        values.put(COLUMN_BUDGET_DATE, budget.getDate());
        values.put(COLUMN_BUDGET_USER_EMAIL, budget.getUserEmail());

        long result = db.insert(TABLE_BUDGETS, null, values);
        db.close();
        return result != -1;
    }

    // Get All Budgets for a specific user
    public List<Budget> getAllBudgets(String userEmail) {
        List<Budget> budgetList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (userEmail == null) {
            db.close();
            return budgetList;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUDGETS + " WHERE " + COLUMN_BUDGET_USER_EMAIL + "=?", new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                Budget budget = new Budget(
                        cursor.getInt(0),    // id
                        cursor.getString(1), // category
                        cursor.getString(2), // note
                        cursor.getDouble(3), // amount
                        cursor.getString(4), // date
                        cursor.getString(5)  // userEmail
                );
                budgetList.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgetList;
    }

    // Retrieve a Budget by ID
    public Budget getBudgetById(int budgetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUDGETS, null, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_BUDGET_ID);
            int categoryIndex = cursor.getColumnIndex(COLUMN_BUDGET_CATEGORY);
            int noteIndex = cursor.getColumnIndex(COLUMN_BUDGET_NOTE);
            int amountIndex = cursor.getColumnIndex(COLUMN_BUDGET_AMOUNT);
            int dateIndex = cursor.getColumnIndex(COLUMN_BUDGET_DATE);
            int userEmailIndex = cursor.getColumnIndex(COLUMN_BUDGET_USER_EMAIL);

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
            return new Budget(id, category, note, amount, date, userEmail);
        }
        cursor.close();
        return null;
    }

    // Update a Budget
    public boolean updateBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BUDGET_CATEGORY, budget.getCategory());
        values.put(COLUMN_BUDGET_NOTE, budget.getNote());
        values.put(COLUMN_BUDGET_AMOUNT, budget.getAmount());
        values.put(COLUMN_BUDGET_DATE, budget.getDate());
        values.put(COLUMN_BUDGET_USER_EMAIL, budget.getUserEmail());

        int rowsAffected = db.update(TABLE_BUDGETS, values, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budget.getId())});
        db.close();
        return rowsAffected > 0;
    }

    // Delete a Budget
    public boolean deleteBudget(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_BUDGETS, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
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

    // Check User Login
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


    // Trong DatabaseHelper.java
    public List<String> getAllCategories(String userEmail) {
        List<String> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        if (userEmail == null) {
            db.close();
            return categoryList;
        }

        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_CATEGORY + " FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USER_EMAIL + "=?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                if (category != null && !category.isEmpty()) {
                    categoryList.add(category);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categoryList;
    }

    public boolean deleteExpensesByCategory(String category, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_EXPENSES,
                COLUMN_CATEGORY + " = ? AND " + COLUMN_USER_EMAIL + " = ?",
                new String[]{category, userEmail});
        db.close();
        return rowsDeleted > 0; // Trả về true nếu có ít nhất 1 bản ghi bị xóa
    }
}