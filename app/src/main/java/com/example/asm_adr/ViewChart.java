package com.example.asm_adr;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class ViewChart extends AppCompatActivity {

    private PieChart pieChart;
    private ImageView backButton;
    private TextView tvTotalExpenses;
    private EditText etStartDate, etEndDate;
    private Button btnApplyFilter;
    private String userEmail;
    private List<Expense> expenseList;
    private Map<String, Double> categoryTotals;
    // Sửa định dạng ngày thành "dd-MM-yyyy" để khớp với "25-04-2025"
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        backButton = findViewById(R.id.imgBack);
        pieChart = findViewById(R.id.pieChart);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);

        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("userEmail", null);
        }

        if (userEmail == null) {
            Toast.makeText(this, "Please log in to view the chart", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }, 1000);
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        expenseList = databaseHelper.getAllExpenses(userEmail);

        // In ra mẫu định dạng ngày để kiểm tra
        for (int i = 0; i < Math.min(3, expenseList.size()); i++) {
            Log.d("ViewChart", "Expense date sample: " + expenseList.get(i).getDate());
        }

        // Hiển thị biểu đồ ban đầu với toàn bộ dữ liệu
        updateChart(expenseList);

        // Sự kiện chọn ngày bắt đầu
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));

        // Sự kiện chọn ngày kết thúc
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // Sự kiện áp dụng bộ lọc
        btnApplyFilter.setOnClickListener(v -> applyDateFilter());

        backButton.setOnClickListener(v -> finish());
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Sửa định dạng ngày thành "dd-MM-yyyy" để khớp với expense.getDate()
                    String date = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Áp dụng bộ lọc theo thời gian
    private void applyDateFilter() {
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Expense> filteredList = new ArrayList<>();
            for (Expense expense : expenseList) {
                try {
                    Date expenseDate = dateFormat.parse(expense.getDate());
                    // Kiểm tra nếu expenseDate nằm trong khoảng startDate và endDate
                    if (!expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                        filteredList.add(expense);
                    }
                } catch (ParseException e) {
                    Log.e("ViewChart", "Error parsing expense date: " + expense.getDate(), e);
                    Toast.makeText(this, "Skipping invalid date in expense: " + expense.getDate(), Toast.LENGTH_SHORT).show();
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No expenses found in this date range", Toast.LENGTH_SHORT).show();
            }

            updateChart(filteredList);

        } catch (ParseException e) {
            Log.e("ViewChart", "Error parsing start/end date: " + startDateStr + " - " + endDateStr, e);
            Toast.makeText(this, "Invalid date format. Use dd-MM-yyyy", Toast.LENGTH_SHORT).show();
        }
    }

    // Cập nhật PieChart và tổng chi phí
    private void updateChart(List<Expense> expenses) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        categoryTotals = new HashMap<>();
        double totalAmount = 0;

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            totalAmount += amount;
        }

        tvTotalExpenses.setText(String.format("Total Expenses: $%.2f", totalAmount));

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            float percentage = totalAmount == 0 ? 0 : (float) (entry.getValue() / totalAmount * 100);
            pieEntries.add(new PieEntry(percentage, entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Category Expenses");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(android.R.color.white);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(pieData);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(android.R.color.black);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(18f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(12f);
        pieChart.animateY(1000);

        pieChart.setMarker(new CustomMarkerView(this, R.layout.marker_view_layout, expenses, categoryTotals));
        pieChart.setDrawMarkers(true);

        pieChart.invalidate();
    }

    // Class MarkerView tùy chỉnh
    private class CustomMarkerView extends MarkerView {
        private TextView tvContent;
        private List<Expense> expenses;
        private Map<String, Double> categoryTotals;

        public CustomMarkerView(Context context, int layoutResource, List<Expense> expenses, Map<String, Double> categoryTotals) {
            super(context, layoutResource);
            this.expenses = expenses;
            this.categoryTotals = categoryTotals;
            tvContent = findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            PieEntry pieEntry = (PieEntry) e;
            String category = pieEntry.getLabel();
            double total = categoryTotals.get(category);

            StringBuilder details = new StringBuilder();
            details.append("Category: ").append(category).append("\n");
            details.append("Total: $").append(String.format("%.2f", total)).append("\n");
            details.append("Expenses:\n");

            for (Expense expense : expenses) {
                if (expense.getCategory().equals(category)) {
                    details.append("- $").append(String.format("%.2f", expense.getAmount()))
                            .append(" (").append(expense.getDate()).append(")\n");
                }
            }

            tvContent.setText(details.toString());
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }
}