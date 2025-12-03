package com.venegas.moneytracker.data.local.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.venegas.moneytracker.data.local.dao.CategoryDao;
import com.venegas.moneytracker.data.local.dao.TransactionDao;
import com.venegas.moneytracker.data.local.entity.CategoryEntity;
import com.venegas.moneytracker.data.local.entity.TransactionEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {TransactionEntity.class, CategoryEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "money_tracker_database"
                            )
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CategoryDao dao = INSTANCE.categoryDao();
                dao.insertAll(getDefaultCategories());
            });
        }
    };

    private static List<CategoryEntity> getDefaultCategories() {
        return Arrays.asList(
                // Categorías de gastos
                new CategoryEntity("Alimentación", "EXPENSE", "🍔", "#FF6B6B"),
                new CategoryEntity("Transporte", "EXPENSE", "🚗", "#4ECDC4"),
                new CategoryEntity("Educación", "EXPENSE", "📚", "#45B7D1"),
                new CategoryEntity("Entretenimiento", "EXPENSE", "🎮", "#F7DC6F"),
                new CategoryEntity("Salud", "EXPENSE", "💊", "#BB8FCE"),
                new CategoryEntity("Otros", "EXPENSE", "📦", "#95A5A6"),

                // Categorías de ingresos
                new CategoryEntity("Salario", "INCOME", "💰", "#2ECC71"),
                new CategoryEntity("Freelance", "INCOME", "💻", "#3498DB"),
                new CategoryEntity("Beca", "INCOME", "🎓", "#9B59B6"),
                new CategoryEntity("Otros", "INCOME", "💵", "#1ABC9C")
        );
    }
}