package com.digitalindonesia.fnb;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalindonesia.fnb.adapter.IngredientAdapter;
import com.digitalindonesia.fnb.cart.CartManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuDetailBottomSheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuDetailBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "MenuDetailBottomSheet";
    private static final String ARG_MENU_ITEM = "arg_menu_item";

    private MenuItem menuItem;
    private int localQuantity = 1; // qty pilihan user di layar ini, min 1

    private TextView tvQuantity, tvTotalPrice, tvTitle, tvDescription, tvCartBadge, tvLikeCount;
    private ImageView btnFavorite, btnLike;

    public static MenuDetailBottomSheet newInstance(com.example.foodorder.model.MenuItem item) {
        MenuDetailBottomSheet fragment = new MenuDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MENU_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_detail_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            menuItem = (MenuItem) getArguments().getSerializable(ARG_MENU_ITEM);
        }
        if (menuItem == null) {
            dismiss();
            return;
        }

        // Kalau item ini sudah ada di cart, mulai dari qty yang sudah ada.
        int existingQty = CartManager.getInstance().getQuantity(menuItem.getId());
        localQuantity = Math.max(1, existingQty);

        ImageView ivHero = view.findViewById(R.id.ivHero);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        ImageView btnCart = view.findViewById(R.id.btnCart);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnFavorite = view.findViewById(R.id.btnFavorite);
        ImageView btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageView btnIncrease = view.findViewById(R.id.btnIncrease);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        RecyclerView rvIngredients = view.findViewById(R.id.rvIngredients);
        View btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnLike = view.findViewById(R.id.btnLike);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);

        ivHero.setImageResource(menuItem.getImageResId());
        tvTitle.setText(menuItem.getName());
        tvDescription.setText(menuItem.getDescription());
        tvLikeCount.setText(String.valueOf(menuItem.getFavoriteCount()));
        updateFavoriteIcon();
        updateQuantityUi();
        updateCartBadge();

        rvIngredients.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvIngredients.setAdapter(new IngredientAdapter(menuItem.getIngredients()));

        btnBack.setOnClickListener(v -> dismiss());
        btnCart.setOnClickListener(v -> dismiss()); // arahkan ke halaman cart jika ada

        btnFavorite.setOnClickListener(v -> {
            menuItem.setFavorite(!menuItem.isFavorite());
            updateFavoriteIcon();
        });

        btnDecrease.setOnClickListener(v -> {
            if (localQuantity > 1) {
                localQuantity--;
                updateQuantityUi();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            localQuantity++;
            updateQuantityUi();
        });

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().setQuantity(menuItem, localQuantity);
            updateCartBadge();
            dismiss(); // kembali ke List Menu; FAB akan otomatis ter-refresh via CartManager listener
        });

        btnLike.setOnClickListener(v -> {
            menuItem.setFavoriteCount(menuItem.getFavoriteCount() + 1);
            tvLikeCount.setText(String.valueOf(menuItem.getFavoriteCount()));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Buat bottom sheet terbuka penuh dari awal (opsional, sesuai referensi UI).
        if (getDialog() instanceof BottomSheetDialog) {
            View bottomSheet = ((BottomSheetDialog) getDialog())
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void updateQuantityUi() {
        tvQuantity.setText(String.valueOf(localQuantity));
        tvTotalPrice.setText(formatPrice(menuItem.getPrice() * localQuantity));
    }

    private void updateFavoriteIcon() {
        btnFavorite.setImageResource(menuItem.isFavorite()
                ? R.drawable.ic_favorite_filled
                : R.drawable.ic_favorite_border);
    }

    private void updateCartBadge() {
        tvCartBadge.setText(String.valueOf(CartManager.getInstance().getTotalQuantity()));
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        return format.format(price).replace("Rp", "Rp ");
    }
}
