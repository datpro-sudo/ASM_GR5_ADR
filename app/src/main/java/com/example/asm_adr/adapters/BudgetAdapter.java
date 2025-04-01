package com.example.asm_adr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.asm_adr.models.Budget;
import com.example.asm_adr.EditBudgetActivity;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgetList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public BudgetAdapter(List<Budget> budgetList, Context context) {
        this.budgetList = budgetList;
        this.databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgetList.get(position);

        // Kiểm tra null trước khi setText
        if (holder.categoryText != null) holder.categoryText.setText(budget.getCategory());
        if (holder.noteText != null) holder.noteText.setText(budget.getNote());
        if (holder.amountText != null) holder.amountText.setText("$" + budget.getAmount());
        if (holder.dateText != null) holder.dateText.setText(budget.getDate());
        if (holder.userEmailText != null) holder.userEmailText.setText(budget.getUserEmail());

        // Handle Edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBudgetActivity.class);
            intent.putExtra("budgetId", budget.getId());
            intent.putExtra("userEmail", budget.getUserEmail());
            context.startActivity(intent);
        });

        // Handle Delete button click with confirmation
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Budget")
                    .setMessage("Are you sure you want to delete this budget?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            Budget budgetToDelete = budgetList.get(adapterPosition);

                            boolean isDeleted = databaseHelper.deleteBudget(budgetToDelete.getId());
                            if (isDeleted) {
                                budgetList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                Toast.makeText(context, "Budget deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error deleting budget", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, noteText, amountText, dateText, userEmailText;
        Button editButton, deleteButton;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            noteText = itemView.findViewById(R.id.noteText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            editButton = itemView.findViewById(R.id.btnEditBudget);
            deleteButton = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}