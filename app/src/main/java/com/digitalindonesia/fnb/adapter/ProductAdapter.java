package com.digitalindonesia.fnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.MenuItem;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public ProductAdapter(List<MenuItem> menuList, OnMenuItemActionListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    /** Listener untuk 2 aksi berbeda: klik body card vs klik tombol '+'. */
    public interface OnMenuItemActionListener {
        void onCardClicked(MenuItem item, int position);
        void onAddClicked(MenuItem item, int position);
    }

    private final List<MenuItem> menuList;
    private final OnMenuItemActionListener listener;

    public MenuAdapter(List<MenuItem> menuList, OnMenuItemActionListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);

        holder.ivMenuImage.setImageResource(item.getImageResId());
        holder.tvMenuTitle.setText(item.getName());
        holder.tvMenuPrice.setText(formatPrice(item.getPrice()));

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

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.cardMenuRoot);
            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            btnAdd = itemView.findViewById(R.id.btnAddMenu);
            tvQtyBadge = itemView.findViewById(R.id.tvQtyBadge);
        }
    }
}

