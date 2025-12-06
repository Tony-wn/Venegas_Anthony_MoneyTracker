package com.venegas.moneytracker.ui.transactions;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.venegas.moneytracker.data.local.entity.CategoryEntity;
import com.venegas.moneytracker.data.local.entity.TransactionEntity;
import com.venegas.moneytracker.data.remote.model.ExchangeRateResponse;
import com.venegas.moneytracker.data.repository.MoneyTrackerRepository;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.DateUtils;
import com.venegas.moneytracker.utils.PreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionViewModel extends AndroidViewModel {

    private MoneyTrackerRepository repository;
    private PreferencesManager preferencesManager;

    private MutableLiveData<String> filterType = new MutableLiveData<>("ALL");
    private LiveData<List<TransactionEntity>> transactions;

    private MutableLiveData<String> operationStatus = new MutableLiveData<>();
    private MutableLiveData<Double> convertedAmount = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new MoneyTrackerRepository(application);
        preferencesManager = PreferencesManager.getInstance(application);

        transactions = Transformations.switchMap(filterType, filter -> {
            switch (filter) {
                case Constants.TYPE_INCOME:
                    return repository.getTransactionsByType(Constants.TYPE_INCOME);
                case Constants.TYPE_EXPENSE:
                    return repository.getTransactionsByType(Constants.TYPE_EXPENSE);
                case "MONTH":
                    long[] range = DateUtils.getCurrentMonthRange(preferencesManager.getStartDay());
                    return repository.getTransactionsByDateRange(range[0], range[1]);
                default:
                    return repository.getAllTransactions();
            }
        });
    }

    // CRUD Operations
    public void insertTransaction(TransactionEntity transaction) {
        repository.insertTransaction(transaction, id -> {
            operationStatus.postValue("INSERTED");
        });
    }

    public void updateTransaction(TransactionEntity transaction) {
        repository.updateTransaction(transaction, () -> {
            operationStatus.postValue("UPDATED");
        });
    }

    public void deleteTransaction(TransactionEntity transaction) {
        repository.deleteTransaction(transaction, () -> {
            operationStatus.postValue("DELETED");
        });
    }

    // Filters
    public void setFilter(String filter) {
        filterType.setValue(filter);
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactions;
    }

    public LiveData<List<CategoryEntity>> getCategoriesByType(String type) {
        return repository.getCategoriesByType(type);
    }

    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    // Currency Converter
    public void convertCurrency(double amount, String baseCurrency, String targetCurrency) {
        repository.getExchangeRates(baseCurrency, new MoneyTrackerRepository.ApiCallback<ExchangeRateResponse>() {
            @Override
            public void onSuccess(ExchangeRateResponse response) {
                Double targetRate = response.getRate(targetCurrency);
                if (targetRate != null) {
                    double convertedValue = amount * targetRate;
                    convertedAmount.postValue(convertedValue);
                } else {
                    operationStatus.postValue("ERROR_RATE_NOT_FOUND");
                }
            }

            @Override
            public void onError(String message) {
                operationStatus.postValue("ERROR_NETWORK");
            }
        });
    }

    public LiveData<Double> getConvertedAmount() {
        return convertedAmount;
    }

    public String getUserCurrency() {
        return preferencesManager.getCurrency();
    }
}