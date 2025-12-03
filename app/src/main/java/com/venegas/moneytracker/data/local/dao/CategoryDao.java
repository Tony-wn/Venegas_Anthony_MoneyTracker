package com.venegas.moneytracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.venegas.moneytracker.data.local.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(CategoryEntity category);

    @Insert
    void insertAll(List<CategoryEntity> categories);

    @Query("SELECT * FROM categories WHERE type = :type")
    LiveData<List<CategoryEntity>> getCategoriesByType(String type);

    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> getAllCategories();

    @Query("DELETE FROM categories")
    void deleteAll();
}
