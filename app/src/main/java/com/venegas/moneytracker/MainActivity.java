package com.venegas.moneytracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NavController navController;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "onCreate: Iniciando MainActivity");

            setContentView(R.layout.activity_main);
            Log.d(TAG, "onCreate: Layout cargado");

            initViews();
            Log.d(TAG, "onCreate: Views inicializadas");

            setupNavigation();
            Log.d(TAG, "onCreate: Navegación configurada");

            setupListeners();
            Log.d(TAG, "onCreate: Listeners configurados");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error en MainActivity", e);
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);

        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);

            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();

                // Configurar bottom navigation y toolbar
                NavigationUI.setupWithNavController(bottomNav, navController);
                NavigationUI.setupWithNavController(toolbar, navController);

                // Controlar visibilidad del FAB según el fragmento
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    Log.d(TAG, "Destino actual: " + destination.getLabel());

                    // Mostrar FAB solo en Dashboard y Transactions
                    if (destination.getId() == R.id.dashboardFragment ||
                            destination.getId() == R.id.transactionsFragment) {
                        fabAddTransaction.show();
                    } else {
                        fabAddTransaction.hide();
                    }

                    // Actualizar título del toolbar
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(destination.getLabel());
                    }
                });

                Log.d(TAG, "setupNavigation: Navegación configurada correctamente");
            } else {
                Log.e(TAG, "setupNavigation: NavHostFragment es null");
                Toast.makeText(this, "Error: No se pudo inicializar la navegación", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "setupNavigation: Error", e);
            e.printStackTrace();
            Toast.makeText(this, "Error en navegación: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        fabAddTransaction.setOnClickListener(v -> {
            try {
                if (navController != null) {
                    // ✅ Verificar que el destino existe antes de navegar
                    if (navController.getCurrentDestination() != null) {
                        Log.d(TAG, "Navegando a addTransactionFragment");
                        navController.navigate(R.id.addTransactionFragment);
                    } else {
                        Log.e(TAG, "No hay destino actual en el NavController");
                        Toast.makeText(this, "Error: No se puede navegar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "NavController es null");
                    Toast.makeText(this, "Error: NavController no inicializado", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al navegar a addTransactionFragment", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && navController.navigateUp()) || super.onSupportNavigateUp();
    }
}