package com.digitalindonesia.fnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.CartLineItem;
import com.digitalindonesia.fnb.model.MenuItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onAddQty(MenuItem item);
        void onMinQty(MenuItem item);
        void onRemove(MenuItem item);
        void onEditPrice(MenuItem item);
        void onEditDiscount(MenuItem item);
        void onEditNote(MenuItem item);
    }

    private final List<CartLineItem> items;
    private final CartActionListener listener;

    public CheckoutCartAdapter(List<CartLineItem> items, CartActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartLineItem cartItem = items.get(position);
        MenuItem item = cartItem.getMenuItem();

        holder.ivItemImage.setImageResource(item.getImageResId());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);

        holder.tvItemName.setText(item.getName());

        // Harga Asli x Qty
        String priceDetail = format.format(item.getCustomPrice()).replace("Rp", "Rp ") + " x " + item.getQuantity();
        if (item.getDiscount() > 0) {
            priceDetail += " (Diskon)";
        }
        holder.tvPriceDetail.setText(priceDetail);

        // Harga Final Total
        holder.tvFinalPrice.setText(format.format(item.getTotalPriceForQuantity()).replace("Rp", "Rp "));
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        // Tampilkan catatan jika ada
        if (item.getNote() != null && !item.getNote().isEmpty()) {
            holder.tvNoteView.setVisibility(View.VISIBLE);
            holder.tvNoteView.setText("Catatan: " + item.getNote());
        } else {
            holder.tvNoteView.setVisibility(View.GONE);
        }

        // Listener Tombol
        holder.btnPlus.setOnClickListener(v -> listener.onAddQty(item));
        holder.btnMinus.setOnClickListener(v -> listener.onMinQty(item));
        holder.btnRemoveItem.setOnClickListener(v -> listener.onRemove(item));

        holder.btnEditPrice.setOnClickListener(v -> listener.onEditPrice(item));
        holder.btnDiscount.setOnClickListener(v -> listener.onEditDiscount(item));
        holder.btnNote.setOnClickListener(v -> listener.onEditNote(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvPriceDetail, tvNoteView, tvQty, tvFinalPrice;
        ImageView ivItemImage, btnRemoveItem;
        View btnEditPrice, btnDiscount, btnNote, btnPlus, btnMinus;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPriceDetail = itemView.findViewById(R.id.tvPriceDetail);
            tvNoteView = itemView.findViewById(R.id.tvNoteView);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
            btnEditPrice = itemView.findViewById(R.id.btnEditPrice);
            btnDiscount = itemView.findViewById(R.id.btnDiscount);
            btnNote = itemView.findViewById(R.id.btnNote);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}