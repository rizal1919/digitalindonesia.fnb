package com.digitalindonesia.fnb.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitalindonesia.fnb.CheckoutStepNavigator;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.adapter.CheckoutCartAdapter;
import com.digitalindonesia.fnb.cart.CartManager;
import com.digitalindonesia.fnb.model.CartLineItem;
import com.digitalindonesia.fnb.model.MenuItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCartFragment extends Fragment implements CartManager.CartListener {

    // ==== Cart Summary Collapsible ====
    private View headerRingkasan;
    private ImageView ivToggleCart;
    private boolean isCartExpanded = false;

    // ==== Empty State & Price Summary ====
    private View llEmptyCart;
    private View llPriceSummary;
    private MaterialButton btnPilihMenuDulu;

    // ==== Pelanggan ====
    private SwitchCompat switchCustomer;
    private EditText etCustomerName;
    private View btnPilihPelangganDb;

    // ==== Meja ====
    private SwitchCompat switchTable;
    private EditText etTableNumber;
    private View btnPilihMejaDb;

    // ==== Lainnya ====
    private EditText etDescription;
    private RecyclerView rvCartItems;
    private TextView tvSubtotal, tvTax, tvGrandTotal;
    private MaterialButton btnLanjutPembayaran;

    private CheckoutStepNavigator navigator;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof CheckoutStepNavigator) {
            navigator = (CheckoutStepNavigator) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        // Daftarkan listener ke CartManager
        CartManager.getInstance().addListener(this);

        setupCollapsibleCart();
        setupCustomerSwitchLogic();
        setupTableSwitchLogic();

        // Pengecekan kondisi awal (Harga & Keranjang Kosong)
        updateCartUI(CartManager.getInstance().getTotalQuantity(), CartManager.getInstance().getTotalPrice());
        setupCartList();

        btnLanjutPembayaran.setOnClickListener(v -> {
            if (navigator != null) navigator.goToNextStep();
        });

        // Tombol jika keranjang kosong
        btnPilihMenuDulu.setOnClickListener(v -> requireActivity().finish());

        // Trigger buka Bottom Sheet Master Database
        btnPilihPelangganDb.setOnClickListener(v -> {
            CustomerBottomSheetFragment bottomSheet = new CustomerBottomSheetFragment();
            bottomSheet.setCustomerSelectionListener(customer -> {
                TextView tvSelectedCustomer = getView().findViewById(R.id.tvSelectedCustomer);
                if (tvSelectedCustomer != null) {
                    tvSelectedCustomer.setText(customer.getFullName());
                    tvSelectedCustomer.setTextColor(Color.BLACK);
                }
            });
            bottomSheet.show(getParentFragmentManager(), CustomerBottomSheetFragment.TAG);
        });

        btnPilihMejaDb.setOnClickListener(v -> {
            TableBottomSheetFragment bottomSheet = new TableBottomSheetFragment();
            bottomSheet.setTableSelectionListener(table -> {
                TextView tvSelectedTable = getView().findViewById(R.id.tvSelectedTable);
                if (tvSelectedTable != null) {
                    tvSelectedTable.setText(table.getTableName());
                    tvSelectedTable.setTextColor(Color.BLACK);
                }
            });
            bottomSheet.show(getParentFragmentManager(), TableBottomSheetFragment.TAG);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CartManager.getInstance().removeListener(this); // Wajib dihapus agar tidak bocor memory
    }

    private void bindViews(View view) {
        headerRingkasan = view.findViewById(R.id.headerRingkasan);
        ivToggleCart = view.findViewById(R.id.ivToggleCart);
        rvCartItems = view.findViewById(R.id.rvCartItems);

        llEmptyCart = view.findViewById(R.id.llEmptyCart);
        llPriceSummary = view.findViewById(R.id.llPriceSummary);
        btnPilihMenuDulu = view.findViewById(R.id.btnPilihMenuDulu);

        switchCustomer = view.findViewById(R.id.switchCustomer);
        etCustomerName = view.findViewById(R.id.etCustomerName);
        btnPilihPelangganDb = view.findViewById(R.id.btnPilihPelangganDb);

        switchTable = view.findViewById(R.id.switchTable);
        etTableNumber = view.findViewById(R.id.etTableNumber);
        btnPilihMejaDb = view.findViewById(R.id.btnPilihMejaDb);

        etDescription = view.findViewById(R.id.etDescription);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvTax = view.findViewById(R.id.tvTax);
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
        btnLanjutPembayaran = view.findViewById(R.id.btnLanjutPembayaran);
    }

    // ==================================================================================
    // Listener ini otomatis dipanggil saat user tekan (+ / - / hapus) di dalam adapter
    // ==================================================================================
    @Override
    public void onCartChanged(int totalQuantity, double totalPrice) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            updateCartUI(totalQuantity, totalPrice);
            setupCartList(); // Refresh adapter
        });
    }

    private void updateCartUI(int totalQuantity, double totalPrice) {
        if (totalQuantity <= 0) {
            // Keranjang Kosong
            llEmptyCart.setVisibility(View.VISIBLE);
            llPriceSummary.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.GONE);
            headerRingkasan.setVisibility(View.GONE);

            btnLanjutPembayaran.setEnabled(false);
            btnLanjutPembayaran.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
        } else {
            // Keranjang Terisi
            llEmptyCart.setVisibility(View.GONE);
            llPriceSummary.setVisibility(View.VISIBLE);
            headerRingkasan.setVisibility(View.VISIBLE);

            rvCartItems.setVisibility(isCartExpanded ? View.VISIBLE : View.GONE);

            btnLanjutPembayaran.setEnabled(true);
            btnLanjutPembayaran.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));

            double tax = totalPrice * 0.11;
            double total = totalPrice + tax;

            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
            format.setMaximumFractionDigits(0);

            tvSubtotal.setText(format.format(totalPrice).replace("Rp", "Rp "));
            tvTax.setText(format.format(tax).replace("Rp", "Rp "));
            tvGrandTotal.setText(format.format(total).replace("Rp", "Rp "));
        }
    }

    private void setupCollapsibleCart() {
        headerRingkasan.setOnClickListener(v -> {
            isCartExpanded = !isCartExpanded;
            if (isCartExpanded) {
                rvCartItems.setVisibility(View.VISIBLE);
                ivToggleCart.setRotation(180f);
            } else {
                rvCartItems.setVisibility(View.GONE);
                ivToggleCart.setRotation(0f);
            }
        });
    }

    private void setupCustomerSwitchLogic() {
        applyCustomerSwitchState(switchCustomer.isChecked());
        switchCustomer.setOnCheckedChangeListener((buttonView, isChecked) -> applyCustomerSwitchState(isChecked));
    }

    private void applyCustomerSwitchState(boolean isChecked) {
        if (isChecked) {
            etCustomerName.setVisibility(View.VISIBLE);
            btnPilihPelangganDb.setVisibility(View.GONE);
        } else {
            etCustomerName.setVisibility(View.GONE);
            etCustomerName.setText("");
            btnPilihPelangganDb.setVisibility(View.VISIBLE);
        }
    }

    private void setupTableSwitchLogic() {
        applyTableSwitchState(switchTable.isChecked());
        switchTable.setOnCheckedChangeListener((buttonView, isChecked) -> applyTableSwitchState(isChecked));
    }

    private void applyTableSwitchState(boolean isChecked) {
        if (isChecked) {
            etTableNumber.setVisibility(View.VISIBLE);
            btnPilihMejaDb.setVisibility(View.GONE);
        } else {
            etTableNumber.setVisibility(View.GONE);
            etTableNumber.setText("");
            btnPilihMejaDb.setVisibility(View.VISIBLE);
        }
    }

    private void setupCartList() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        List<MenuItem> cartItems = CartManager.getInstance().getCartItems();
        List<CartLineItem> displayItems = new ArrayList<>();

        if (cartItems != null) {
            for (MenuItem item : cartItems) {
                displayItems.add(new CartLineItem(item.getName(), "Normal", item.getPrice(), item.getQuantity(), item.getImageResId()));
            }
        }
        rvCartItems.setAdapter(new CheckoutCartAdapter(displayItems));
    }

    public String getSelectedCustomerName() {
        if (switchCustomer.isChecked()) return etCustomerName.getText().toString().trim();
        return null;
    }

    public String getSelectedTableNumber() {
        if (switchTable.isChecked()) return etTableNumber.getText().toString().trim();
        return null;
    }

    public String getOrderDescription() {
        return etDescription.getText().toString().trim();
    }
}