package com.example.asm_adr;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChart extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvDescription;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Khởi tạo PieChart và TextView
        pieChart = findViewById(R.id.pieChart);
        tvDescription = findViewById(R.id.tvDescription);

        // Lấy email người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInEmail", null);
        if (userEmail == null) {
            finish();
            return;
        }

        // Lấy dữ liệu chi tiêu từ cơ sở dữ liệu
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Expense> expenseList = databaseHelper.getAllExpenses(userEmail);

        // Tạo danh sách PieEntry và lưu trữ tổng số tiền theo danh mục
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalAmount = 0;

        for (Expense expense : expenseList) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            totalAmount += amount;
        }

        // Tạo PieEntry cho mỗi danh mục
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            float percentage = (float) (entry.getValue() / totalAmount * 100);
            pieEntries.add(new PieEntry(percentage, entry.getKey()));
        }

        // Cấu hình PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Màu sắc đa dạng
        dataSet.setValueTextSize(14f); // Kích thước chữ phần trăm
        dataSet.setValueTextColor(android.R.color.white); // Màu chữ trắng
        dataSet.setSliceSpace(3f); // Khoảng cách giữa các phần
        dataSet.setSelectionShift(10f); // Độ nổi bật khi chọn (dù không dùng listener)

        // Cấu hình PieData
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);

        // Tùy chỉnh PieChart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false); // Tắt mô tả mặc định
        pieChart.setEntryLabelTextSize(12f); // Kích thước chữ nhãn danh mục
        pieChart.setEntryLabelColor(android.R.color.black); // Màu chữ nhãn
        pieChart.setCenterText("Expense Breakdown");
        pieChart.setCenterTextSize(18f);
        pieChart.setDrawHoleEnabled(true); // Kiểu donut
        pieChart.setHoleRadius(40f); // Bán kính lỗ
        pieChart.setTransparentCircleRadius(45f); // Vòng trong suốt
        pieChart.setRotationEnabled(true); // Cho phép xoay biểu đồ
        pieChart.setHighlightPerTapEnabled(true); // Vẫn giữ nổi bật khi chạm, nhưng không xử lý sự kiện

        // Tùy chỉnh legend (chú thích)
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);

        // Hiển thị tổng quan trong TextView thay vì chi tiết khi chạm
        if (expenseList.isEmpty()) {
            tvDescription.setText("No expenses found");
        } else {
            StringBuilder summary = new StringBuilder("Total Expenses: $" + String.format("%.2f", totalAmount) + "\n");
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                summary.append(entry.getKey())
                        .append(": $")
                        .append(String.format("%.2f", entry.getValue()))
                        .append("\n");
            }
            tvDescription.setText(summary.toString());
        }

        pieChart.invalidate(); // Refresh biểu đồ
    }

    // Formatter để hiển thị phần trăm đẹp hơn
    public static class PercentFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.format("%.1f%%", value);
        }
    }
}