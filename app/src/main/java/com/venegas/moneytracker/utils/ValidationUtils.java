package com.venegas.moneytracker.utils;

import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class ValidationUtils {

    public static boolean isFieldEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }

    public static boolean validateNotEmpty(TextInputLayout inputLayout, String errorMessage) {
        EditText editText = inputLayout.getEditText();
        if (editText == null || isFieldEmpty(editText)) {
            inputLayout.setError(errorMessage);
            return false;
        }
        inputLayout.setError(null);
        return true;
    }

    public static boolean validateAmount(TextInputLayout inputLayout) {
        EditText editText = inputLayout.getEditText();
        if (editText == null || isFieldEmpty(editText)) {
            inputLayout.setError("Ingrese un monto");
            return false;
        }

        try {
            double amount = Double.parseDouble(editText.getText().toString().trim());
            if (amount <= 0) {
                inputLayout.setError("El monto debe ser mayor a 0");
                return false;
            }
            inputLayout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            inputLayout.setError("Monto inválido");
            return false;
        }
    }

    public static boolean validateUsername(TextInputLayout inputLayout) {
        EditText editText = inputLayout.getEditText();
        if (editText == null || isFieldEmpty(editText)) {
            inputLayout.setError("Ingrese su nombre");
            return false;
        }

        String name = editText.getText().toString().trim();
        if (name.length() < 3) {
            inputLayout.setError("El nombre debe tener al menos 3 caracteres");
            return false;
        }

        inputLayout.setError(null);
        return true;
    }

    public static boolean validateDayOfMonth(EditText editText) {
        if (isFieldEmpty(editText)) {
            return false;
        }

        try {
            int day = Integer.parseInt(editText.getText().toString().trim());
            return day >= 1 && day <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getText(EditText editText) {
        if (editText == null) return "";
        return editText.getText().toString().trim();
    }

    public static double getDouble(EditText editText, double defaultValue) {
        try {
            return Double.parseDouble(getText(editText));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int getInt(EditText editText, int defaultValue) {
        try {
            return Integer.parseInt(getText(editText));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}