package com.venegas.moneytracker.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.venegas.moneytracker.data.remote.model.ExchangeRateResponse;
import com.venegas.moneytracker.data.repository.MoneyTrackerRepository;
import com.venegas.moneytracker.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends AndroidViewModel {

    private PreferencesManager preferencesManager;
    private MoneyTrackerRepository repository;

    private MutableLiveData<String> operationStatus = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        preferencesManager = PreferencesManager.getInstance(application);
        repository = new MoneyTrackerRepository(application);
    }

    // Getters para datos actuales
    public String getUserName() {
        return preferencesManager.getUserName();
    }

    public double getMonthlyBudget() {
        return preferencesManager.getMonthlyBudget();
    }

    public String getCurrency() {
        return preferencesManager.getCurrency();
    }

    public int getStartDay() {
        return preferencesManager.getStartDay();
    }

    public int getAlertThreshold() {
        return preferencesManager.getAlertThreshold();
    }

    // Setters para guardar cambios
    public void saveSettings(String name, double budget, String currency, int startDay, int threshold) {
        preferencesManager.saveUserName(name);
        preferencesManager.saveMonthlyBudget(budget);
        preferencesManager.saveCurrency(currency);
        preferencesManager.saveStartDay(startDay);
        preferencesManager.saveAlertThreshold(threshold);

        operationStatus.postValue("SETTINGS_SAVED");
    }

    public void updateExchangeRates() {
        String currency = preferencesManager.getCurrency();

        repository.getExchangeRates(currency, new MoneyTrackerRepository.ApiCallback<ExchangeRateResponse>() {
            @Override
            public void onSuccess(ExchangeRateResponse response) {
                operationStatus.postValue("RATES_UPDATED");
            }

            @Override
            public void onError(String message) {
                operationStatus.postValue("ERROR_API_OR_NETWORK");
            }
        });
    }

    public void resetData() {
        repository.deleteAllTransactions(() -> {
            operationStatus.postValue("DATA_RESET");
        });
    }

    public MutableLiveData<String> getOperationStatus() {
        return operationStatus;
    }
}