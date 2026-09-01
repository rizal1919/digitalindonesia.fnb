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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.CartViewHolder> {

    private final List<CartLineItem> items;

    public CheckoutCartAdapter(List<CartLineItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartLineItem item = items.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);

        holder.tvItemName.setText(item.getName());
        holder.tvItemVariant.setText(item.getVariant());
        holder.tvItemPrice.setText(format.format(item.getPrice()));
        holder.tvQty.setText(String.format(Locale.getDefault(), "%02d", item.getQuantity()));
        holder.ivItemImage.setImageResource(item.getImageResId());

        holder.tvRemoveItem.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                items.remove(pos);
                notifyItemRemoved(pos);
                // TODO: panggil ulang kalkulasi Subtotal/Pajak/Total di fragment setelah item dihapus
            }
        });

        holder.tvEditItem.setOnClickListener(v -> {
            // TODO: buka MenuDetailBottomSheet untuk edit varian/catatan item ini
        });

        holder.spinnerQty.setOnClickListener(v -> {
            // TODO: tampilkan dropdown/dialog pemilihan kuantitas, lalu update item.setQuantity(...)
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        TextView tvItemName, tvItemVariant, tvItemPrice, tvQty, tvEditItem, tvRemoveItem;
        View spinnerQty;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemVariant = itemView.findViewById(R.id.tvItemVariant);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvEditItem = itemView.findViewById(R.id.tvEditItem);
            tvRemoveItem = itemView.findViewById(R.id.tvRemoveItem);
            spinnerQty = itemView.findViewById(R.id.spinnerQty);
        }
    }
}
