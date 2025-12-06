package com.venegas.moneytracker.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.venegas.moneytracker.data.local.dao.CategorySum;
import com.venegas.moneytracker.data.repository.MoneyTrackerRepository;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.DateUtils;
import com.venegas.moneytracker.utils.PreferencesManager;

import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {

    private MoneyTrackerRepository repository;
    private PreferencesManager preferencesManager;

    private MutableLiveData<Integer> totalTransactions = new MutableLiveData<>(0);
    private MutableLiveData<Double> dailyAverage = new MutableLiveData<>(0.0);
    private MutableLiveData<String> selectedPeriod = new MutableLiveData<>("THIS_MONTH");

    private long startDate;
    private long endDate;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        repository = new MoneyTrackerRepository(application);
        preferencesManager = PreferencesManager.getInstance(application);

        calculateCurrentMonth();
        loadStatistics();
    }

    private void calculateCurrentMonth() {
        int startDay = preferencesManager.getStartDay();
        long[] range = DateUtils.getCurrentMonthRange(startDay);
        startDate = range[0];
        endDate = range[1];
    }

    private void loadStatistics() {
        // Calcular total de transacciones y promedio diario
        repository.getSumByType(Constants.TYPE_EXPENSE, startDate, endDate, totalExpense -> {
            if (totalExpense > 0) {
                int days = DateUtils.getRemainingDays(preferencesManager.getStartDay());
                int totalDays = 30; // Aproximado
                int elapsedDays = totalDays - days;

                if (elapsedDays > 0) {
                    double average = totalExpense / elapsedDays;
                    dailyAverage.postValue(average);
                }
            }
        });
    }

    public LiveData<List<CategorySum>> getExpensesByCategory() {
        return repository.getExpensesByCategoryLiveData(startDate, endDate);
    }

    public void setPeriod(String period) {
        selectedPeriod.setValue(period);

        if ("LAST_MONTH".equals(period)) {
            // Calcular mes anterior
            int startDay = preferencesManager.getStartDay();
            long[] range = DateUtils.getCurrentMonthRange(startDay);

            // Restar un mes
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTimeInMillis(range[0]);
            calendar.add(java.util.Calendar.MONTH, -1);
            startDate = calendar.getTimeInMillis();

            calendar.setTimeInMillis(range[1]);
            calendar.add(java.util.Calendar.MONTH, -1);
            endDate = calendar.getTimeInMillis();
        } else {
            calculateCurrentMonth();
        }

        loadStatistics();
    }

    public LiveData<Integer> getTotalTransactions() {
        return totalTransactions;
    }

    public LiveData<Double> getDailyAverage() {
        return dailyAverage;
    }

    public LiveData<String> getSelectedPeriod() {
        return selectedPeriod;
    }

    public String getCurrency() {
        return preferencesManager.getCurrency();
    }
}