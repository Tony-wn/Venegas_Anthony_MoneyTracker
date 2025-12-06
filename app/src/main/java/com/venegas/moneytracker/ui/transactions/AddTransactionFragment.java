package com.venegas.moneytracker.ui.transactions;

import android.app.DatePickerDialog;
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
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.data.local.entity.TransactionEntity;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.CurrencyUtils;
import com.venegas.moneytracker.utils.DateUtils;
import com.venegas.moneytracker.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTransactionFragment extends Fragment {

    private TransactionViewModel viewModel;

    // Views
    private MaterialButtonToggleGroup toggleType;
    private MaterialButton btnIncome, btnExpense;
    private TextInputLayout tilAmount, tilCategory, tilDescription, tilDate, tilPaymentMethod;
    // AÑADIDO: Vistas para el nuevo spinner de moneda
    private TextInputLayout tilTargetCurrency;
    private AutoCompleteTextView actTargetCurrency;
    private TextInputEditText etAmount, etDescription, etDate;
    private AutoCompleteTextView actCategory, actPaymentMethod;
    private MaterialButton btnSave, btnCancel, btnDelete, btnConvert;
    private MaterialCardView cardConverter;
    private TextView tvConvertedAmount;

    // Data
    private String selectedType = Constants.TYPE_EXPENSE;
    private long selectedDate = System.currentTimeMillis();
    private long transactionId = -1;
    private boolean isEditMode = false;
    private TransactionEntity currentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Obtener argumentos si es modo edición
        if (getArguments() != null) {
            transactionId = getArguments().getLong("transactionId", -1);
            isEditMode = getArguments().getBoolean("isEditMode", false);
        }

        initViews(view);
        setupObservers();
        setupListeners();
        setupPaymentMethodSpinner();
        // AÑADIDO: Configurar el nuevo spinner de moneda
        setupCurrencyConverterSpinner();

        // Seleccionar tipo por defecto
        btnExpense.setChecked(true);
    }

    private void initViews(View view) {
        toggleType = view.findViewById(R.id.toggle_type);
        btnIncome = view.findViewById(R.id.btn_income);
        btnExpense = view.findViewById(R.id.btn_expense);

        tilAmount = view.findViewById(R.id.til_amount);
        tilCategory = view.findViewById(R.id.til_category);
        tilDescription = view.findViewById(R.id.til_description);
        tilDate = view.findViewById(R.id.til_date);
        tilPaymentMethod = view.findViewById(R.id.til_payment_method);
        // AÑADIDO: Inicializar vistas del convertidor
        tilTargetCurrency = view.findViewById(R.id.til_target_currency);
        actTargetCurrency = view.findViewById(R.id.act_target_currency);

        etAmount = view.findViewById(R.id.et_amount);
        etDescription = view.findViewById(R.id.et_description);
        etDate = view.findViewById(R.id.et_date);

        actCategory = view.findViewById(R.id.act_category);
        actPaymentMethod = view.findViewById(R.id.act_payment_method);

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnConvert = view.findViewById(R.id.btn_convert);

        cardConverter = view.findViewById(R.id.card_converter);
        tvConvertedAmount = view.findViewById(R.id.tv_converted_amount);

        // Mostrar botón eliminar solo en modo edición
        if (isEditMode) {
            btnDelete.setVisibility(View.VISIBLE);
        }

        // Establecer fecha actual por defecto
        etDate.setText(DateUtils.formatDate(selectedDate));
    }

    private void setupObservers() {
        // Observar categorías según el tipo
        viewModel.getCategoriesByType(selectedType).observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                List<String> categoryNames = new ArrayList<>();
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames.add(categories.get(i).getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categoryNames
                );
                actCategory.setAdapter(adapter);
            }
        });

        // Observar estado de operaciones
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                switch (status) {
                    case "INSERTED":
                        Toast.makeText(getContext(), R.string.transaction_added, Toast.LENGTH_SHORT).show();
                        navigateBack();
                        break;
                    case "UPDATED":
                        Toast.makeText(getContext(), R.string.transaction_updated, Toast.LENGTH_SHORT).show();
                        navigateBack();
                        break;
                    case "DELETED":
                        Toast.makeText(getContext(), R.string.transaction_deleted, Toast.LENGTH_SHORT).show();
                        navigateBack();
                        break;
                    case "ERROR_NETWORK":
                        Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // Observar conversión de moneda
        // MODIFICADO: Para mostrar la moneda correcta
        viewModel.getConvertedAmount().observe(getViewLifecycleOwner(), converted -> {
            if (converted != null) {
                String targetCurrency = actTargetCurrency.getText().toString();
                String formatted = CurrencyUtils.formatAmount(converted, targetCurrency);
                tvConvertedAmount.setText("≈ " + formatted); // El formato ya incluye el símbolo/código
                tvConvertedAmount.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        // Toggle tipo de transacción
        toggleType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_income) {
                    selectedType = Constants.TYPE_INCOME;
                } else if (checkedId == R.id.btn_expense) {
                    selectedType = Constants.TYPE_EXPENSE;
                }

                // Actualizar categorías
                actCategory.setText("", false);
                viewModel.getCategoriesByType(selectedType);
            }
        });

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Botones
        btnSave.setOnClickListener(v -> saveTransaction());
        btnCancel.setOnClickListener(v -> navigateBack());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void setupPaymentMethodSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.PAYMENT_METHODS
        );
        actPaymentMethod.setAdapter(adapter);
        actPaymentMethod.setText(Constants.PAYMENT_METHODS[0], false);
    }

    private void setupCurrencyConverterSpinner() {
        List<String> allCurrencies = CurrencyUtils.SUPPORTED_CURRENCIES;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                allCurrencies
        );
        actTargetCurrency.setAdapter(adapter);

        // Establecer un valor por defecto (ej: "USD")
        String defaultTargetCurrency = "USD";
        if (!viewModel.getUserCurrency().equalsIgnoreCase(defaultTargetCurrency)) {
            actTargetCurrency.setText(defaultTargetCurrency, false);
        } else {
            // Si la moneda del usuario ya es USD, poner otra por defecto (ej: EUR)
            actTargetCurrency.setText("EUR", false);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = selected.getTimeInMillis();
                    etDate.setText(DateUtils.formatDate(selectedDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void saveTransaction() {
        // Validar campos
        if (!validateFields()) {
            return;
        }

        // Obtener valores
        double amount = ValidationUtils.getDouble(etAmount, 0);
        String category = actCategory.getText().toString();
        String description = ValidationUtils.getText(etDescription);
        String paymentMethod = actPaymentMethod.getText().toString();

        if (isEditMode && currentTransaction != null) {
            // Actualizar transacción existente
            currentTransaction.setType(selectedType);
            currentTransaction.setAmount(amount);
            currentTransaction.setCategory(category);
            currentTransaction.setDescription(description);
            currentTransaction.setDate(selectedDate);
            currentTransaction.setPaymentMethod(paymentMethod);

            viewModel.updateTransaction(currentTransaction);
        } else {
            // Crear nueva transacción
            TransactionEntity transaction = new TransactionEntity(
                    selectedType,
                    amount,
                    category,
                    description,
                    selectedDate,
                    paymentMethod
            );

            viewModel.insertTransaction(transaction);
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (!ValidationUtils.validateAmount(tilAmount)) {
            isValid = false;
        }

        if (actCategory.getText().toString().isEmpty()) {
            tilCategory.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else {
            tilCategory.setError(null);
        }

        if (actPaymentMethod.getText().toString().isEmpty()) {
            tilPaymentMethod.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else {
            tilPaymentMethod.setError(null);
        }

        return isValid;
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete)
                .setMessage("¿Estás seguro de eliminar esta transacción?")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (currentTransaction != null) {
                        viewModel.deleteTransaction(currentTransaction);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void convertCurrency() {
        if (!ValidationUtils.validateAmount(tilAmount)) {
            return;
        }

        String targetCurrency = actTargetCurrency.getText().toString();
        if (targetCurrency.isEmpty()) {
            tilTargetCurrency.setError("Seleccione una moneda");
            return;
        } else {
            tilTargetCurrency.setError(null);
        }

        double amount = ValidationUtils.getDouble(etAmount, 0);
        String userCurrency = viewModel.getUserCurrency();

        if (userCurrency.equalsIgnoreCase(targetCurrency)) {
            Toast.makeText(getContext(), "No se puede convertir a la misma moneda", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.convertCurrency(amount, userCurrency, targetCurrency);
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).navigateUp();
    }
}
