package com.venegas.moneytracker.utils;

public class Constants {

    public static final String TYPE_INCOME = "INCOME";
    public static final String TYPE_EXPENSE = "EXPENSE";

    public static final String[] PAYMENT_METHODS = {
            "Efectivo",
            "Tarjeta de Débito",
            "Tarjeta de Crédito",
            "Transferencia",
            "Otro"
    };

    public static final int ALERT_THRESHOLD_WARNING = 80;
    public static final int ALERT_THRESHOLD_DANGER = 100;

    public static final int RANGE_WEEK = 7;
    public static final int RANGE_MONTH = 30;
    public static final int RANGE_YEAR = 365;

    public static final String PREF_FIRST_TIME = "first_time";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_CURRENCY = "currency";

    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
    public static final String EXTRA_TRANSACTION_TYPE = "transaction_type";
    public static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    public static final int REQUEST_ADD_TRANSACTION = 1001;
    public static final int REQUEST_EDIT_TRANSACTION = 1002;

    public static final int RESULT_TRANSACTION_ADDED = 2001;
    public static final int RESULT_TRANSACTION_UPDATED = 2002;
    public static final int RESULT_TRANSACTION_DELETED = 2003;

    // Chart Colors
    public static final int[] CHART_COLORS = {
            0xFFFF6B6B, // Rojo
            0xFF4ECDC4, // Turquesa
            0xFF45B7D1, // Azul
            0xFFF7DC6F, // Amarillo
            0xFFBB8FCE, // Morado
            0xFF95A5A6, // Gris
            0xFF2ECC71, // Verde
            0xFF3498DB, // Azul oscuro
            0xFF9B59B6, // Púrpura
            0xFF1ABC9C  // Verde azulado
    };
}