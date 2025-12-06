package com.venegas.moneytracker.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.utils.Constants;

import java.util.ArrayList;

public class TransactionsFragment extends Fragment {

    private TransactionViewModel viewModel;

    // Views
    private RecyclerView rvTransactions;
    private LinearLayout layoutEmptyTransactions;
    private Chip chipAll, chipIncome, chipExpense, chipThisMonth;

    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        rvTransactions = view.findViewById(R.id.rv_transactions);
        layoutEmptyTransactions = view.findViewById(R.id.layout_empty_transactions);
        chipAll = view.findViewById(R.id.chip_all);
        chipIncome = view.findViewById(R.id.chip_income);
        chipExpense = view.findViewById(R.id.chip_expense);
        chipThisMonth = view.findViewById(R.id.chip_this_month);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(new ArrayList<>(), viewModel.getUserCurrency());
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(adapter);

        // Click listener
        adapter.setOnItemClickListener(transaction -> {
            Bundle args = new Bundle();
            args.putLong("transactionId", transaction.getId());
            args.putBoolean("isEditMode", true);
            Navigation.findNavController(requireView())
                    .navigate(R.id.addTransactionFragment, args);
        });

        // Swipe to delete
        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmation(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvTransactions);
    }

    private void showDeleteConfirmation(int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar transacción")
                .setMessage("¿Estás seguro de eliminar esta transacción?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    adapter.deleteItem(position, transaction -> {
                        viewModel.deleteTransaction(transaction);
                    });
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    adapter.notifyItemChanged(position);
                })
                .setOnCancelListener(dialog -> {
                    adapter.notifyItemChanged(position);
                })
                .show();
    }

    private void setupObservers() {
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                adapter.updateTransactions(transactions);
                rvTransactions.setVisibility(View.VISIBLE);
                layoutEmptyTransactions.setVisibility(View.GONE);
            } else {
                rvTransactions.setVisibility(View.GONE);
                layoutEmptyTransactions.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                switch (status) {
                    case "DELETED":
                        Snackbar.make(requireView(), "Transacción eliminada", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setupListeners() {
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setFilter("ALL");
            }
        });

        chipIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setFilter(Constants.TYPE_INCOME);
            }
        });

        chipExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setFilter(Constants.TYPE_EXPENSE);
            }
        });

        chipThisMonth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setFilter("MONTH");
            }
        });
    }
}