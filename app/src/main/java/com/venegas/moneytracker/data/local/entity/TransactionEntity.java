package com.venegas.moneytracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    private String type; // "INCOME" o "EXPENSE"
    private double amount;
    private String category;
    private String description;
    private long date; // timestamp
    private String paymentMethod;
    private long createdAt;

    public TransactionEntity(String type, double amount, String category, String description, long date, String paymentMethod) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.createdAt = System.currentTimeMillis();
    }
}
