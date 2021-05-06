package com.example.expensestracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.expensestracker.databinding.ExpenseItemBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpensesViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ExpenseItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.expense_item, parent, false);

        return new ExpensesViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesViewHolder holder, int position) {

        Expense expense = expenseList.get(position);
        holder.binding.tvDesc.setText(expense.getDescription());
        holder.binding.tvValue.setText(expense.getValue());


    }

    @Override
    public int getItemCount() {
        return expenseList == null ? 0 : expenseList.size();
    }

    public static class ExpensesViewHolder extends RecyclerView.ViewHolder {

        private final ExpenseItemBinding binding;

        ExpensesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }


    }

}
