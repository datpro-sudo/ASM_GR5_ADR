package com.example.asm_adr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Thêm import cho ImageView
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
    private String userEmail;

    public ExpenseAdapter(List<Expense> expenseList, DatabaseHelper databaseHelper, Context context) {
        this.expenseList = expenseList;
        this.databaseHelper = databaseHelper;
        this.context = context;

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
        holder.amountText.setText("$" + String.format("%.2f", expense.getAmount()));
        holder.dateText.setText(expense.getDate());

        // Handle Edit ImageView click
        holder.editImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("expenseId", expense.getId());
            context.startActivity(intent);
        });

        // Handle Delete ImageView click with confirmation
        holder.deleteImageView.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            Expense expenseToDelete = expenseList.get(adapterPosition);

                            boolean isDeleted = databaseHelper.deleteExpense(expenseToDelete.getId());
                            if (isDeleted) {
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

    public void updateExpenses(List<Expense> newExpenseList) {
        this.expenseList = newExpenseList;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, noteText, amountText, dateText;
        ImageView editImageView, deleteImageView; // Thay Button bằng ImageView

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            noteText = itemView.findViewById(R.id.noteText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            editImageView = itemView.findViewById(R.id.btnEditExpenses); // Giữ id cũ
            deleteImageView = itemView.findViewById(R.id.btnDeleteExpenses); // Giữ id cũ
        }
    }
}