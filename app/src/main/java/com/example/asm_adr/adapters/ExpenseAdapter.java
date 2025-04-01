package com.example.asm_adr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_adr.R;
import com.example.asm_adr.database.DatabaseHelper;
import com.example.asm_adr.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public ExpenseAdapter(List<Expense> expenseList, DatabaseHelper databaseHelper, Context context) {
        this.expenseList = expenseList;
        this.databaseHelper = databaseHelper;
        this.context = context;
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
        holder.amountText.setText("$" + expense.getAmount());
        holder.dateText.setText(expense.getDate());

        // Handle delete button click with confirmation
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Use holder.getAdapterPosition() instead of position to get the current adapter position
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
                        }
                    })
                    .setNegativeButton("No", null)  // Do nothing if "No" is clicked
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, noteText, amountText, dateText;
        Button deleteButton;  // Changed to Button for delete

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            noteText = itemView.findViewById(R.id.noteText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteButton = itemView.findViewById(R.id.btnDeleteExpense);  // Ensure this is the correct reference for Button
        }
    }
}
