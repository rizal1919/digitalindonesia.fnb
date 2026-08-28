package com.digitalindonesia.fnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.MenuModel;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuModel> menuList;
    private OnItemClickListener listener;

    // Interface untuk menangani klik
    public interface OnItemClickListener {
        void onItemClick(MenuModel menu);
    }

    public MenuAdapter(List<MenuModel> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pastikan R.layout.namalayout mengarah ke file XML yang baru kamu buat
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_dashboard, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuModel menu = menuList.get(position);

        // Set data ke View
        holder.ivIcon.setImageResource(menu.getIconRes());
        holder.tvTitle.setText(menu.getTitle());

        // Aksi ketika kotak menu diklik
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(menu);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList == null ? 0 : menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            // UPDATE: Sesuaikan dengan ID di layout item_quick_menu.xml yang baru
            ivIcon = itemView.findViewById(R.id.ivMenuIcon);
            tvTitle = itemView.findViewById(R.id.tvMenuTitle);

            // Catatan: tvSubtitle dihapus dari inisialisasi karena
            // di desain minimalis yang baru, teks hanya menggunakan tvTitle di luar kotak.
        }
    }
}