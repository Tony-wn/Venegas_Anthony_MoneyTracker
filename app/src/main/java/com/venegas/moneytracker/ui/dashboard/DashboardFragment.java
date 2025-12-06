package com.venegas.moneytracker.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.ui.transactions.TransactionAdapter;
import com.venegas.moneytracker.utils.CurrencyUtils;
import com.venegas.moneytracker.utils.DateUtils;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;

    // Views
    private TextView tvGreeting, tvCurrentMonth;
    private TextView tvBalanceAmount, tvIncomeAmount, tvExpenseAmount;
    private TextView tvBudgetPercentage, tvRemainingBudget, tvDaysRemaining;
    private LinearProgressIndicator progressBudget;
    private RecyclerView rvRecentTransactions;
    private LinearLayout layoutEmptyState;
    private TextView tvViewAll;

    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupObservers();
        setupListeners();

        updateGreeting();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
        tvBalanceAmount = view.findViewById(R.id.tv_balance_amount);
        tvIncomeAmount = view.findViewById(R.id.tv_income_amount);
        tvExpenseAmount = view.findViewById(R.id.tv_expense_amount);
        tvBudgetPercentage = view.findViewById(R.id.tv_budget_percentage);
        tvRemainingBudget = view.findViewById(R.id.tv_remaining_budget);
        tvDaysRemaining = view.findViewById(R.id.tv_days_remaining);
        progressBudget = view.findViewById(R.id.progress_budget);
        rvRecentTransactions = view.findViewById(R.id.rv_recent_transactions);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tvViewAll = view.findViewById(R.id.tv_view_all);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(new ArrayList<>(), viewModel.getCurrency());
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentTransactions.setAdapter(adapter);

        adapter.setOnItemClickListener(transaction -> {
            // Navegar a editar transacción
            Bundle args = new Bundle();
            args.putLong("transactionId", transaction.getId());
            args.putBoolean("isEditMode", true);
            Navigation.findNavController(requireView())
                    .navigate(R.id.addTransactionFragment, args);
        });
    }

    private void setupObservers() {
        // Balance
        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            String formatted = CurrencyUtils.formatAmount(balance, viewModel.getCurrency());
            tvBalanceAmount.setText(formatted);
        });

        // Ingresos
        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            String formatted = CurrencyUtils.formatAmount(income, viewModel.getCurrency());
            tvIncomeAmount.setText(formatted);
        });

        // Gastos
        viewModel.getTotalExpenses().observe(getViewLifecycleOwner(), expenses -> {
            String formatted = CurrencyUtils.formatAmount(expenses, viewModel.getCurrency());
            tvExpenseAmount.setText(formatted);
        });

        // Progreso de presupuesto
        viewModel.getBudgetProgress().observe(getViewLifecycleOwner(), progress -> {
            progressBudget.setProgress(progress);
            tvBudgetPercentage.setText(progress + "%");

            double budget = viewModel.getMonthlyBudget();
            double expenses = viewModel.getTotalExpenses().getValue() != null ?
                    viewModel.getTotalExpenses().getValue() : 0.0;
            double remaining = budget - expenses;

            String formattedRemaining = CurrencyUtils.formatAmount(remaining, viewModel.getCurrency());
            tvRemainingBudget.setText("Restante: " + formattedRemaining);
        });

        // Transacciones recientes
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                // Mostrar solo las 5 más recientes
                int limit = Math.min(transactions.size(), 5);
                adapter.updateTransactions(transactions.subList(0, limit));

                rvRecentTransactions.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvRecentTransactions.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        tvViewAll.setOnClickListener(v -> {
            // Navegar a la pestaña de transacciones
            Navigation.findNavController(v).navigate(R.id.transactionsFragment);
        });
    }

    private void updateGreeting() {
        String userName = viewModel.getUserName();
        String greeting = getString(R.string.dashboard_greeting, userName);
        tvGreeting.setText(greeting);

        long currentTime = System.currentTimeMillis();
        String monthYear = DateUtils.formatMonth(currentTime);
        tvCurrentMonth.setText(monthYear);

        int remainingDays = viewModel.getRemainingDays();
        tvDaysRemaining.setText(remainingDays + " días restantes");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refrescar datos cuando se vuelve al fragmento
        viewModel.refreshData();
    }
}