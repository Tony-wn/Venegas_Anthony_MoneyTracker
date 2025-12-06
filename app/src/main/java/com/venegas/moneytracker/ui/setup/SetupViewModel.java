package com.venegas.moneytracker.ui.setup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.venegas.moneytracker.utils.PreferencesManager;

public class SetupViewModel extends AndroidViewModel {

    private PreferencesManager preferencesManager;

    public SetupViewModel(@NonNull Application application) {
        super(application);
        preferencesManager = PreferencesManager.getInstance(application);
    }

    public void saveUserData(String name, double budget, String currency, int startDay) {
        preferencesManager.saveUserName(name);
        preferencesManager.saveMonthlyBudget(budget);
        preferencesManager.saveCurrency(currency);
        preferencesManager.saveStartDay(startDay);
        preferencesManager.setFirstTimeComplete();
    }

    public boolean isFirstTime() {
        return preferencesManager.isFirstTime();
    }
}