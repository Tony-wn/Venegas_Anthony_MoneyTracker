package com.venegas.moneytracker.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.venegas.moneytracker.data.local.entity.TransactionEntity;
import com.venegas.moneytracker.data.repository.MoneyTrackerRepository;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.DateUtils;
import com.venegas.moneytracker.utils.PreferencesManager;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private MoneyTrackerRepository repository;
    private PreferencesManager preferencesManager;

    private MutableLiveData<Double> totalIncome = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> totalExpenses = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> balance = new MutableLiveData<>(0.0);
    private MutableLiveData<Integer> budgetProgress = new MutableLiveData<>(0);
    private MutableLiveData<String> budgetStatus = new MutableLiveData<>("");

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new MoneyTrackerRepository(application);
        preferencesManager = PreferencesManager.getInstance(application);
        loadDashboardData();
    }

    private void loadDashboardData() {
        int startDay = preferencesManager.getStartDay();
        long[] range = DateUtils.getCurrentMonthRange(startDay);
        long startDate = range[0];
        long endDate = range[1];

        // Calcular ingresos
        repository.getSumByType(Constants.TYPE_INCOME, startDate, endDate, income -> {
            totalIncome.postValue(income);
            updateBalance();
        });

        // Calcular gastos
        repository.getSumByType(Constants.TYPE_EXPENSE, startDate, endDate, expenses -> {
            totalExpenses.postValue(expenses);
            updateBalance();
            updateBudgetProgress(expenses);
        });
    }

    private void updateBalance() {
        Double income = totalIncome.getValue();
        Double expenses = totalExpenses.getValue();
        if (income != null && expenses != null) {
            balance.postValue(income - expenses);
        }
    }

    private void updateBudgetProgress(double expenses) {
        double budget = preferencesManager.getMonthlyBudget();
        if (budget > 0) {
            int progress = (int) ((expenses / budget) * 100);
            budgetProgress.postValue(progress);

            int threshold = preferencesManager.getAlertThreshold();
            if (progress >= 100) {
                budgetStatus.postValue("¡Has superado tu presupuesto!");
            } else if (progress >= threshold) {
                budgetStatus.postValue("¡Cuidado! Estás cerca del límite");
            } else {
                budgetStatus.postValue("Dentro del presupuesto");
            }
        }
    }

    public LiveData<List<TransactionEntity>> getRecentTransactions() {
        int startDay = preferencesManager.getStartDay();
        long[] range = DateUtils.getCurrentMonthRange(startDay);
        return repository.getTransactionsByDateRange(range[0], range[1]);
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpenses() {
        return totalExpenses;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }

    public LiveData<Integer> getBudgetProgress() {
        return budgetProgress;
    }

    public LiveData<String> getBudgetStatus() {
        return budgetStatus;
    }

    public String getUserName() {
        return preferencesManager.getUserName();
    }

    public String getCurrency() {
        return preferencesManager.getCurrency();
    }

    public double getMonthlyBudget() {
        return preferencesManager.getMonthlyBudget();
    }

    public int getRemainingDays() {
        return DateUtils.getRemainingDays(preferencesManager.getStartDay());
    }

    public void refreshData() {
        loadDashboardData();
    }
}