package com.venegas.moneytracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREFS_NAME = "MoneyTrackerPrefs";

    // Keys
    private static final String KEY_FIRST_TIME = "first_time";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MONTHLY_BUDGET = "monthly_budget";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_START_DAY = "start_day";
    private static final String KEY_ALERT_THRESHOLD = "alert_threshold";

    private SharedPreferences prefs;
    private static PreferencesManager instance;

    private PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isFirstTime() {
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTimeComplete() {
        prefs.edit().putBoolean(KEY_FIRST_TIME, false).apply();
    }

    public void saveUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void saveMonthlyBudget(double budget) {
        prefs.edit().putFloat(KEY_MONTHLY_BUDGET, (float) budget).apply();
    }

    public double getMonthlyBudget() {
        return prefs.getFloat(KEY_MONTHLY_BUDGET, 0f);
    }

    public void saveCurrency(String currency) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply();
    }

    public String getCurrency() {
        return prefs.getString(KEY_CURRENCY, "USD");
    }

    public void saveStartDay(int day) {
        prefs.edit().putInt(KEY_START_DAY, day).apply();
    }

    public int getStartDay() {
        return prefs.getInt(KEY_START_DAY, 1);
    }

    public void saveAlertThreshold(int threshold) {
        prefs.edit().putInt(KEY_ALERT_THRESHOLD, threshold).apply();
    }

    public int getAlertThreshold() {
        return prefs.getInt(KEY_ALERT_THRESHOLD, 80);
    }

    public void resetAllData() {
        prefs.edit().clear().apply();
    }

    public boolean isSetupComplete() {
        return !isFirstTime() &&
                !getUserName().isEmpty() &&
                getMonthlyBudget() > 0;
    }
}