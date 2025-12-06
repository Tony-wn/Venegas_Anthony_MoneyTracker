package com.venegas.moneytracker.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.utils.CurrencyUtils;
import com.venegas.moneytracker.utils.ValidationUtils;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;

    // Views
    private TextInputLayout tilName, tilBudget, tilStartDay, tilCurrency;
    private TextInputEditText etName, etBudget, etStartDay;
    private AutoCompleteTextView actCurrency;
    private Slider sliderThreshold;
    private TextView tvThresholdValue;
    private MaterialButton btnUpdateRates, btnResetData, btnSaveSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        initViews(view);
        loadCurrentSettings();
        setupCurrencySpinner();
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        tilName = view.findViewById(R.id.til_name);
        tilBudget = view.findViewById(R.id.til_budget);
        tilStartDay = view.findViewById(R.id.til_start_day);
        tilCurrency = view.findViewById(R.id.til_currency);

        etName = view.findViewById(R.id.et_name);
        etBudget = view.findViewById(R.id.et_budget);
        etStartDay = view.findViewById(R.id.et_start_day);
        actCurrency = view.findViewById(R.id.act_currency);

        sliderThreshold = view.findViewById(R.id.slider_threshold);
        tvThresholdValue = view.findViewById(R.id.tv_threshold_value);

        btnUpdateRates = view.findViewById(R.id.btn_update_rates);
        btnResetData = view.findViewById(R.id.btn_reset_data);
        btnSaveSettings = view.findViewById(R.id.btn_save_settings);
    }

    private void loadCurrentSettings() {
        etName.setText(viewModel.getUserName());
        etBudget.setText(String.valueOf(viewModel.getMonthlyBudget()));
        etStartDay.setText(String.valueOf(viewModel.getStartDay()));

        int threshold = viewModel.getAlertThreshold();
        sliderThreshold.setValue(threshold);
        tvThresholdValue.setText(threshold + "%");
    }

    private void setupCurrencySpinner() {
        String[] currencies = CurrencyUtils.getCurrencyNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                currencies
        );
        actCurrency.setAdapter(adapter);

        // Establecer moneda actual
        String currentCurrency = viewModel.getCurrency();
        String currencyDisplay = currentCurrency + " - " + CurrencyUtils.getCurrencyName(currentCurrency);
        actCurrency.setText(currencyDisplay, false);
    }

    private void setupObservers() {
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                switch (status) {
                    case "SETTINGS_SAVED":
                        Toast.makeText(getContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
                        break;
                    case "RATES_UPDATED":
                        Toast.makeText(getContext(), "Tasas actualizadas", Toast.LENGTH_SHORT).show();
                        break;
                    case "DATA_RESET":
                        Toast.makeText(getContext(), R.string.data_reset_success, Toast.LENGTH_SHORT).show();
                        break;
                    case "ERROR_NETWORK":
                        Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                        break;
                    case "ERROR_API":
                        Toast.makeText(getContext(), "Error al conectar con el servicio", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setupListeners() {
        // Slider threshold
        sliderThreshold.addOnChangeListener((slider, value, fromUser) -> {
            tvThresholdValue.setText((int) value + "%");
        });

        // Botones
        btnUpdateRates.setOnClickListener(v -> viewModel.updateExchangeRates());
        btnResetData.setOnClickListener(v -> showResetConfirmation());
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        // Validar campos
        if (!validateFields()) {
            return;
        }

        // Obtener valores
        String name = ValidationUtils.getText(etName);
        double budget = ValidationUtils.getDouble(etBudget, 0);
        String currencyText = actCurrency.getText().toString();
        String currency = currencyText.substring(0, 3); // Extraer código
        int startDay = ValidationUtils.getInt(etStartDay, 1);
        int threshold = (int) sliderThreshold.getValue();

        // Guardar
        viewModel.saveSettings(name, budget, currency, startDay, threshold);
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (!ValidationUtils.validateUsername(tilName)) {
            isValid = false;
        }

        if (!ValidationUtils.validateAmount(tilBudget)) {
            isValid = false;
        }

        if (!ValidationUtils.validateDayOfMonth(etStartDay)) {
            tilStartDay.setError("Día debe estar entre 1 y 31");
            isValid = false;
        } else {
            tilStartDay.setError(null);
        }

        return isValid;
    }

    private void showResetConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.reset_data)
                .setMessage(R.string.reset_data_warning)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    viewModel.resetData();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}