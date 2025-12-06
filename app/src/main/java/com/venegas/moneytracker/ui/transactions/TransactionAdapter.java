package com.venegas.moneytracker.ui.transactions;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.venegas.moneytracker.R;
import com.venegas.moneytracker.data.local.entity.TransactionEntity;
import com.venegas.moneytracker.utils.Constants;
import com.venegas.moneytracker.utils.CurrencyUtils;
import com.venegas.moneytracker.utils.DateUtils;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<TransactionEntity> transactions;
    private String currency;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TransactionEntity transaction);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(TransactionEntity transaction);
    }

    public TransactionAdapter(List<TransactionEntity> transactions, String currency) {
        this.transactions = transactions;
        this.currency = currency;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateTransactions(List<TransactionEntity> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    public void deleteItem(int position, OnItemDeleteListener deleteListener) {
        TransactionEntity transaction = transactions.get(position);
        transactions.remove(position);
        notifyItemRemoved(position);

        if (deleteListener != null) {
            deleteListener.onItemDelete(transaction);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionEntity transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardIconBackground;
        private TextView tvCategoryIcon;
        private TextView tvCategory;
        private TextView tvDescription;
        private TextView tvDate;
        private TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            cardIconBackground = itemView.findViewById(R.id.card_icon_background);
            tvCategoryIcon = itemView.findViewById(R.id.tv_category_icon);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(transactions.get(position));
                }
            });
        }

        public void bind(TransactionEntity transaction) {
            // Categoría
            tvCategory.setText(transaction.getCategory());

            // Descripción
            if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
                tvDescription.setText(transaction.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Fecha
            String formattedDate = DateUtils.formatDate(transaction.getDate());
            tvDate.setText(formattedDate);

            // Monto
            String formattedAmount = CurrencyUtils.formatAmount(
                    transaction.getAmount(),
                    currency
            );

            if (transaction.getType().equals(Constants.TYPE_INCOME)) {
                tvAmount.setText("+" + formattedAmount);
                tvAmount.setTextColor(itemView.getContext().getColor(R.color.income_green));
            } else {
                tvAmount.setText("-" + formattedAmount);
                tvAmount.setTextColor(itemView.getContext().getColor(R.color.expense_red));
            }

            // Icono de categoría (emoji o símbolo)
            String icon = getCategoryIcon(transaction.getCategory());
            tvCategoryIcon.setText(icon);

            // Color de fondo del icono
            int backgroundColor = getCategoryColor(transaction.getCategory());
            cardIconBackground.setCardBackgroundColor(backgroundColor);
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
                case "Salario":
                    return itemView.getContext().getColor(R.color.income_green);
                case "Freelance":
                    return itemView.getContext().getColor(R.color.info_blue);
                case "Beca":
                    return itemView.getContext().getColor(R.color.md_theme_tertiary);
                default:
                    return itemView.getContext().getColor(R.color.chart_6);
            }
        }
    }
}