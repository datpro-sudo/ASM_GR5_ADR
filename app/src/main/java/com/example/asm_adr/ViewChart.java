package com.example.asm_adr;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ViewChart extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Tạo đối tượng PieChart
        pieChart = findViewById(R.id.pieChart);

        // Lấy dữ liệu chi tiêu từ cơ sở dữ liệu
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Expense> expenseList = databaseHelper.getAllExpenses();

        // Tạo danh sách PieEntry cho biểu đồ
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        double totalAmount = 0;

        // Tính tổng số tiền và phần trăm cho từng category
        for (Expense expense : expenseList) {
            totalAmount += expense.getAmount();
        }

        for (Expense expense : expenseList) {
            // Tính phần trăm cho từng category
            float percentage = (float) (expense.getAmount() / totalAmount * 100);
            pieEntries.add(new PieEntry(percentage, expense.getCategory()));
        }

        // Cấu hình biểu đồ
        PieDataSet dataSet = new PieDataSet(pieEntries, "Category Expenses");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);  // Chọn màu sắc cho các phần của biểu đồ

        // Tạo PieData và thiết lập vào biểu đồ
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Thiết lập các thuộc tính khác cho PieChart
        pieChart.setUsePercentValues(true);
        pieChart.invalidate(); // Refresh the chart
    }
}
