package com.digitalindonesia.fnb.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.MenuItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MenuViewHolder> {

    /** Listener untuk 2 aksi berbeda: klik body card vs klik tombol '+'. */
//    public interface OnMenuItemActionListener {
//        void onCardClicked(MenuItem item, int position);
//        void onAddClicked(MenuItem item, int position);
//    }

    // 1. UPDATE INTERFACE
    public interface OnMenuItemActionListener {
        void onCardClicked(MenuItem item, int position);
        void onIncreaseClicked(MenuItem item, int position);
        void onDecreaseClicked(MenuItem item, int position);
    }

    private final List<MenuItem> menuList;
    private final OnMenuItemActionListener listener;
    private boolean isGridView = true;

    public ProductAdapter(List<MenuItem> menuList, OnMenuItemActionListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    // FUNGSI BARU: Ini yang tadi bikin error "Cannot resolve method"
    public void setGridView(boolean isGridView) {
        this.isGridView = isGridView;
    }

    @Override
    public int getItemViewType(int position) {
        // Mengembalikan angka 1 untuk Grid, dan angka 2 untuk List.
        // Dengan begini, sistem tidak akan mencampuradukkan cache-nya.
        return isGridView ? 1 : 2;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // (Kode di dalam sini tetap sama persis seperti sebelumnya)
        int layoutId = isGridView ? R.layout.item_menu : R.layout.item_menu_list;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);

        holder.ivMenuImage.setImageResource(item.getImageResId());
        holder.tvMenuTitle.setText(item.getName());
        holder.tvMenuPrice.setText(formatPrice(item.getPrice()));

        // Logika Tampilan Stok
        holder.tvMenuStock.setText("Stok: " + item.getStock());
        if (item.getStock() <= 0) {
            holder.tvMenuStock.setTextColor(Color.parseColor("#E53935"));
            holder.btnAddInitial.setAlpha(0.4f);
            holder.btnAddInitial.setEnabled(false);
            holder.btnIncrease.setEnabled(false); // Matikan tombol plus di kapsul jika stok habis
        } else {
            holder.tvMenuStock.setTextColor(Color.parseColor("#424242"));
            holder.btnAddInitial.setAlpha(1.0f);
            holder.btnAddInitial.setEnabled(true);
            holder.btnIncrease.setEnabled(true);
        }

        // 2. LOGIKA TOGGLE UI KAPSUL VS TOMBOL AWAL
        int currentQty = item.getQuantity();
        if (currentQty > 0) {
            // Sembunyikan tombol awal, munculkan kapsul (+/-)
            holder.btnAddInitial.setVisibility(View.GONE);
            holder.llQuantityControl.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.valueOf(currentQty));
        } else {
            // Tampilkan tombol awal, sembunyikan kapsul
            holder.btnAddInitial.setVisibility(View.VISIBLE);
            holder.llQuantityControl.setVisibility(View.GONE);
        }

        // 3. PASANG LISTENER
        holder.cardRoot.setOnClickListener(v -> {
            if (listener != null) listener.onCardClicked(item, holder.getBindingAdapterPosition());
        });

        // Klik tombol (+) awal ATAU klik (+) di dalam kapsul akan memicu fungsi yang sama
        holder.btnAddInitial.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseClicked(item, holder.getBindingAdapterPosition());
        });
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseClicked(item, holder.getBindingAdapterPosition());
        });

        // Klik tombol (-) di dalam kapsul
        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null) listener.onDecreaseClicked(item, holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    /** Dipanggil setelah quantity sebuah item berubah, supaya badge ter-refresh tanpa full rebind. */
    public void refreshItem(int position) {
        if (position >= 0 && position < menuList.size()) {
            notifyItemChanged(position);
        }
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        return format.format(price).replace("Rp", "Rp ");
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        View cardRoot;
        ImageView ivMenuImage;
        TextView tvMenuTitle;
        TextView tvMenuPrice;
        TextView tvMenuStock;

        // 4. DAFTARKAN WIDGET BARU
        ImageView btnAddInitial;
        View llQuantityControl;
        ImageView btnDecrease;
        ImageView btnIncrease;
        TextView tvQuantity;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardMenuRoot);
            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvMenuStock = itemView.findViewById(R.id.tvMenuStock);

            // HAPUS btnAdd & tvQtyBadge lama, ganti dengan yang baru
            btnAddInitial = itemView.findViewById(R.id.btnAddInitial);
            llQuantityControl = itemView.findViewById(R.id.llQuantityControl);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}