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
    public interface OnMenuItemActionListener {
        void onCardClicked(MenuItem item, int position);
        void onAddClicked(MenuItem item, int position);
    }

    private final List<MenuItem> menuList;
    private final OnMenuItemActionListener listener;

    // VARIABEL BARU: Untuk menyimpan status view (Grid atau List)
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
            // Jika stok habis: teks merah, tombol pudar dan tidak bisa diklik
            holder.tvMenuStock.setTextColor(Color.parseColor("#E53935"));
            holder.btnAdd.setAlpha(0.4f);
            holder.btnAdd.setEnabled(false);
        } else {
            // Jika stok ada: teks normal, tombol aktif
            holder.tvMenuStock.setTextColor(Color.parseColor("#424242"));
            holder.btnAdd.setAlpha(1.0f);
            holder.btnAdd.setEnabled(true);
        }

        // Badge kuantitas kecil di pojok tombol '+' jika item sudah ada di cart
        if (item.getQuantity() > 0) {
            holder.tvQtyBadge.setVisibility(View.VISIBLE);
            holder.tvQtyBadge.setText(String.valueOf(item.getQuantity()));
        } else {
            holder.tvQtyBadge.setVisibility(View.GONE);
        }

        holder.cardRoot.setOnClickListener(v -> {
            if (listener != null) listener.onCardClicked(item, holder.getBindingAdapterPosition());
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddClicked(item, holder.getBindingAdapterPosition());
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
        ImageView btnAdd;
        TextView tvQtyBadge;
        TextView tvMenuStock; // Widget Text untuk Stok

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardMenuRoot);
            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            btnAdd = itemView.findViewById(R.id.btnAddMenu);
            tvQtyBadge = itemView.findViewById(R.id.tvQtyBadge);
            tvMenuStock = itemView.findViewById(R.id.tvMenuStock); // Pastikan ini di-bind
        }
    }
}