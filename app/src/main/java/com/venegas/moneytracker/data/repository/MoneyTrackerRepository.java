package com.venegas.moneytracker.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.venegas.moneytracker.BuildConfig;
import com.venegas.moneytracker.data.local.dao.CategoryDao;
import com.venegas.moneytracker.data.local.dao.CategorySum;
import com.venegas.moneytracker.data.local.dao.TransactionDao;
import com.venegas.moneytracker.data.local.database.AppDatabase;
import com.venegas.moneytracker.data.local.entity.CategoryEntity;
import com.venegas.moneytracker.data.local.entity.TransactionEntity;
import com.venegas.moneytracker.data.remote.RetrofitClient;
import com.venegas.moneytracker.data.remote.model.ExchangeRateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoneyTrackerRepository {

    private TransactionDao transactionDao;
    private CategoryDao categoryDao;
    private LiveData<List<TransactionEntity>> allTransactions;

    private final String apiKey;

    public MoneyTrackerRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        categoryDao = database.categoryDao();
        allTransactions = transactionDao.getAllTransactions();


        this.apiKey = BuildConfig.EXCHANGE_API_KEY;
    }

    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<TransactionEntity>> getTransactionsByDateRange(long startDate, long endDate) {
        return transactionDao.getTransactionsByDateRange(startDate, endDate);
    }

    public LiveData<List<TransactionEntity>> getTransactionsByType(String type) {
        return transactionDao.getTransactionsByType(type);
    }

    public void insertTransaction(TransactionEntity transaction, OnTransactionInsertedListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = transactionDao.insert(transaction);
            if (listener != null) {
                listener.onInserted(id);
            }
        });
    }

    public void updateTransaction(TransactionEntity transaction, OnOperationCompleteListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.update(transaction);
            if (listener != null) {
                listener.onComplete();
            }
        });
    }

    public void deleteTransaction(TransactionEntity transaction, OnOperationCompleteListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.delete(transaction);
            if (listener != null) {
                listener.onComplete();
            }
        });
    }

    public void getSumByType(String type, long startDate, long endDate, OnSumCalculatedListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Double sum = transactionDao.getSumByType(type, startDate, endDate);
            if (listener != null) {
                listener.onCalculated(sum != null ? sum : 0.0);
            }
        });
    }

    public void getExpensesByCategory(long startDate, long endDate, OnCategorySumListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
        });
    }

    public LiveData<List<CategorySum>> getExpensesByCategoryLiveData(long startDate, long endDate) {
        return transactionDao.getExpensesByCategory(startDate, endDate);
    }

    public void deleteAllTransactions(OnOperationCompleteListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.deleteAll();
            if (listener != null) {
                listener.onComplete();
            }
        });
    }

    public LiveData<List<CategoryEntity>> getCategoriesByType(String type) {
        return categoryDao.getCategoriesByType(type);
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public void getExchangeRates(String baseCurrency, ApiCallback<ExchangeRateResponse> callback) {
        Call<ExchangeRateResponse> call = RetrofitClient.getInstance().getApi().getExchangeRates(apiKey, baseCurrency);

        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeRateResponse> call, @NonNull Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeRateResponse> call, @NonNull Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String message);
    }

    public interface OnTransactionInsertedListener {
        void onInserted(long id);
    }

    public interface OnOperationCompleteListener {
        void onComplete();
    }

    public interface OnSumCalculatedListener {
        void onCalculated(double sum);
    }

    public interface OnCategorySumListener {
        void onResult(List<CategorySum> categorySums);
    }
}
