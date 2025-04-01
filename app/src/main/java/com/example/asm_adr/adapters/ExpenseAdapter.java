package com.example.asm_adr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_adr.EditExpenseActivity;
import com.example.asm_adr.R;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private DatabaseHelper databaseHelper;
    private Context context;
    private String userEmail; // Thêm biến để kiểm tra quyền sở hữu

    public ExpenseAdapter(List<Expense> expenseList, DatabaseHelper databaseHelper, Context context) {
        this.expenseList = expenseList;
        this.databaseHelper = databaseHelper;
        this.context = context;

        // Lấy email người dùng hiện tại từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.userEmail = prefs.getString("loggedInEmail", null);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.categoryText.setText(expense.getCategory());
        holder.noteText.setText(expense.getNote());
        holder.amountText.setText("$" + String.format("%.2f", expense.getAmount())); // Định dạng số tiền
        holder.dateText.setText(expense.getDate());

        // Hiển thị userEmail (tùy chọn, nếu giao diện hỗ trợ)
        // holder.userEmailText.setText(expense.getUserEmail()); // Uncomment nếu thêm TextView trong layout

        // Kiểm tra quyền sở hữu trước khi hiển thị nút chỉnh sửa/xóa
        if (userEmail != null && userEmail.equals(expense.getUserEmail())) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Handle Edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("expenseId", expense.getId());
            context.startActivity(intent);
        });

        // Handle delete button click with confirmation
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            Expense expenseToDelete = expenseList.get(adapterPosition);

                            // Delete the expense from the database
                            boolean isDeleted = databaseHelper.deleteExpense(expenseToDelete.getId());
                            if (isDeleted) {
                                // Remove from the list and update RecyclerView
                                expenseList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error deleting expense", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Cung cấp phương thức để cập nhật danh sách chi tiêu
    public void updateExpenses(List<Expense> newExpenseList) {
        this.expenseList = newExpenseList;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, noteText, amountText, dateText; // userEmailText nếu cần
        Button editButton, deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            noteText = itemView.findViewById(R.id.noteText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            // userEmailText = itemView.findViewById(R.id.userEmailText); // Uncomment nếu thêm vào layout
            editButton = itemView.findViewById(R.id.btnEditExpense);
            deleteButton = itemView.findViewById(R.id.btnDeleteExpense);
        }
    }
}