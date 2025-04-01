package com.example.asm_adr;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChart extends AppCompatActivity {

    private PieChart pieChart;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = findViewById(R.id.pieChart);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("loggedInEmail", null);
        if (userEmail == null) {
            finish();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Expense> expenseList = databaseHelper.getAllExpenses(userEmail);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalAmount = 0;

        for (Expense expense : expenseList) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            totalAmount += amount;
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            float percentage = (float) (entry.getValue() / totalAmount * 100);
            pieEntries.add(new PieEntry(percentage, entry.getKey()));
        }

        // Cấu hình PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, "Category Expenses");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS); // Màu sắc tươi sáng hơn
        dataSet.setValueTextSize(14f); // Kích thước chữ phần trăm
        dataSet.setValueTextColor(android.R.color.white); // Màu chữ trắng
        dataSet.setSliceSpace(3f); // Khoảng cách giữa các phần
        dataSet.setSelectionShift(10f); // Độ nổi lên khi chọn

        // Cấu hình PieData
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart)); // Hiển thị % chính xác
        pieChart.setData(pieData);

        // Cấu hình PieChart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true); // Tạo lỗ ở giữa (donut style)
        pieChart.setHoleColor(android.R.color.transparent); // Màu lỗ trong suốt
        pieChart.setHoleRadius(50f); // Bán kính lỗ
        pieChart.setTransparentCircleRadius(55f); // Vòng trong suốt bao quanh lỗ
        pieChart.setEntryLabelTextSize(12f); // Kích thước nhãn danh mục
        pieChart.setEntryLabelColor(android.R.color.black); // Màu chữ nhãn
        pieChart.setCenterText("Expenses"); // Chữ ở giữa
        pieChart.setCenterTextSize(18f); // Kích thước chữ ở giữa
        pieChart.getLegend().setEnabled(true); // Hiển thị chú thích
        pieChart.getLegend().setTextSize(12f); // Kích thước chữ chú thích
        pieChart.animateY(1000); // Hiệu ứng xuất hiện
        pieChart.invalidate(); // Refresh biểu đồ
    }
}