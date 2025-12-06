package com.venegas.moneytracker.ui.statistics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.data.local.dao.CategorySum;
import com.venegas.moneytracker.utils.CurrencyUtils;

import java.util.List;

public class CategoryStatAdapter extends RecyclerView.Adapter<CategoryStatAdapter.CategoryStatViewHolder> {

    private List<CategorySum> categorySums;
    private String currency;
    private double totalAmount = 0;

    public CategoryStatAdapter(List<CategorySum> categorySums, String currency) {
        this.categorySums = categorySums;
        this.currency = currency;
        calculateTotal();
    }

    public void updateCategories(List<CategorySum> newCategorySums) {
        this.categorySums = newCategorySums;
        calculateTotal();
        notifyDataSetChanged();
    }

    private void calculateTotal() {
        totalAmount = 0;
        if (categorySums != null) {
            for (CategorySum categorySum : categorySums) {
                totalAmount += categorySum.total;
            }
        }
    }

    @NonNull
    @Override
    public CategoryStatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_stat, parent, false);
        return new CategoryStatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryStatViewHolder holder, int position) {
        CategorySum categorySum = categorySums.get(position);
        holder.bind(categorySum);
    }

    @Override
    public int getItemCount() {
        return categorySums != null ? categorySums.size() : 0;
    }

    class CategoryStatViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCategoryIcon;
        private TextView tvCategoryName;
        private TextView tvAmount;
        private TextView tvPercentage;
        private LinearProgressIndicator progressCategory;

        public CategoryStatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryIcon = itemView.findViewById(R.id.tv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            progressCategory = itemView.findViewById(R.id.progress_category);
        }

        public void bind(CategorySum categorySum) {
            // Nombre de categoría
            tvCategoryName.setText(categorySum.category);

            // Icono de categoría
            String icon = getCategoryIcon(categorySum.category);
            tvCategoryIcon.setText(icon);

            // Monto
            String formattedAmount = CurrencyUtils.formatAmount(categorySum.total, currency);
            tvAmount.setText(formattedAmount);

            // Calcular porcentaje
            int percentage = 0;
            if (totalAmount > 0) {
                percentage = (int) ((categorySum.total / totalAmount) * 100);
            }

            tvPercentage.setText(percentage + "%");
            progressCategory.setProgress(percentage);

            // Color del progress bar según categoría
            int color = getCategoryColor(categorySum.category);
            progressCategory.setIndicatorColor(color);
        }

        private String getCategoryIcon(String category) {
            // Mapeo de categorías a emojis
            switch (category) {
                case "Alimentación":
                    return "🍔";
                case "Transporte":
                    return "🚗";
                case "Educación":
                    return "📚";
                case "Entretenimiento":
                    return "🎮";
                case "Salud":
                    return "💊";
                case "Salario":
                    return "💰";
                case "Freelance":
                    return "💻";
                case "Beca":
                    return "🎓";
                default:
                    return "📦";
            }
        }

        private int getCategoryColor(String category) {
            // Mapeo de categorías a colores
            switch (category) {
                case "Alimentación":
                    return itemView.getContext().getColor(R.color.chart_1);
                case "Transporte":
                    return itemView.getContext().getColor(R.color.chart_2);
                case "Educación":
                    return itemView.getContext().getColor(R.color.chart_3);
                case "Entretenimiento":
                    return itemView.getContext().getColor(R.color.chart_4);
                case "Salud":
                    return itemView.getContext().getColor(R.color.chart_5);
                default:
                    return itemView.getContext().getColor(R.color.chart_6);
            }
        }
    }
}