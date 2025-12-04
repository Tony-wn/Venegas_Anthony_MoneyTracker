package com.venegas.moneytracker.utils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CurrencyUtils {

    public static final List<String> SUPPORTED_CURRENCIES = Arrays.asList(
            "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "MXN", "BRL",
            "ARS", "COP", "CLP", "PEN", "VES"
    );

    public static String formatAmount(double amount, String currencyCode) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            format.setCurrency(Currency.getInstance(currencyCode));
            return format.format(amount);
        } catch (Exception e) {
            return currencyCode + " " + String.format(Locale.US, "%.2f", amount);
        }
    }

    public static String formatAmountWithoutSymbol(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    public static double convertAmount(double amount, double exchangeRate) {
        return amount * exchangeRate;
    }

    public static String getCurrencySymbol(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getSymbol(Locale.US);
        } catch (Exception e) {
            return currencyCode;
        }
    }

    public static String getCurrencyName(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getDisplayName(Locale.getDefault());
        } catch (Exception e) {
            return currencyCode;
        }
    }

    public static boolean isValidCurrency(String currencyCode) {
        return SUPPORTED_CURRENCIES.contains(currencyCode);
    }

    public static String[] getCurrencyNames() {
        String[] names = new String[SUPPORTED_CURRENCIES.size()];
        for (int i = 0; i < SUPPORTED_CURRENCIES.size(); i++) {
            String code = SUPPORTED_CURRENCIES.get(i);
            names[i] = code + " - " + getCurrencyName(code);
        }
        return names;
    }
}