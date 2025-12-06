package com.venegas.moneytracker.ui.setup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.venegas.moneytracker.MainActivity;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.utils.CurrencyUtils;
import com.venegas.moneytracker.utils.ValidationUtils;

public class SetupActivity extends AppCompatActivity {

    private SetupViewModel viewModel;

    // Views
    private TextInputLayout tilName, tilBudget, tilCurrency, tilStartDay;
    private TextInputEditText etName, etBudget, etStartDay;
    private AutoCompleteTextView actCurrency;
    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(SetupViewModel.class);

        // Verificar si ya completó el setup
        if (!viewModel.isFirstTime()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_setup);

        initViews();
        setupCurrencySpinner();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.til_name);
        tilBudget = findViewById(R.id.til_budget);
        tilCurrency = findViewById(R.id.til_currency);
        tilStartDay = findViewById(R.id.til_start_day);

        etName = findViewById(R.id.et_name);
        etBudget = findViewById(R.id.et_budget);
        etStartDay = findViewById(R.id.et_start_day);
        actCurrency = findViewById(R.id.act_currency);

        btnStart = findViewById(R.id.btn_start);
    }

    private void setupCurrencySpinner() {
        String[] currencies = CurrencyUtils.getCurrencyNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                currencies
        );
        actCurrency.setAdapter(adapter);

        // Seleccionar USD por defecto
        actCurrency.setText("USD - United States Dollar", false);
    }

    private void setupListeners() {
        btnStart.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        // Validar campos
        boolean isValid = true;

        if (!ValidationUtils.validateUsername(tilName)) {
            isValid = false;
        }

        if (!ValidationUtils.validateAmount(tilBudget)) {
            isValid = false;
        }

        if (actCurrency.getText().toString().isEmpty()) {
            tilCurrency.setError("Seleccione una moneda");
            isValid = false;
        } else {
            tilCurrency.setError(null);
        }

        if (!ValidationUtils.validateDayOfMonth(etStartDay)) {
            tilStartDay.setError("Día debe estar entre 1 y 31");
            isValid = false;
        } else {
            tilStartDay.setError(null);
        }

        if (!isValid) {
            return;
        }

        // Obtener valores
        String name = ValidationUtils.getText(etName);
        double budget = ValidationUtils.getDouble(etBudget, 0);
        String currencyText = actCurrency.getText().toString();
        String currency = currencyText.substring(0, 3); // Extraer código (USD, EUR, etc)
        int startDay = ValidationUtils.getInt(etStartDay, 1);

        // Guardar datos
        viewModel.saveUserData(name, budget, currency, startDay);

        Toast.makeText(this, "¡Configuración completada!", Toast.LENGTH_SHORT).show();

        // Navegar a MainActivity
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}