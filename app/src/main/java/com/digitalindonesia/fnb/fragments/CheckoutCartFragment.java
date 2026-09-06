package com.digitalindonesia.fnb.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.CheckoutStepNavigator;
import com.digitalindonesia.fnb.HoldSuccessActivity;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.adapter.CheckoutCartAdapter;
import com.digitalindonesia.fnb.cart.CartManager;
import com.digitalindonesia.fnb.model.CartLineItem;
import com.digitalindonesia.fnb.model.HoldManager;
import com.digitalindonesia.fnb.model.MenuItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCartFragment extends Fragment implements CartManager.CartListener {

    private View headerRingkasan;
    private ImageView ivToggleCart;
    private boolean isCartExpanded = false;

    private View llEmptyCart;
    private View llPriceSummary;
    private MaterialButton btnPilihMenuDulu;

    private SwitchCompat switchCustomer;
    private EditText etCustomerName;
    private View btnPilihPelangganDb;

    private SwitchCompat switchTable;
    private EditText etTableNumber;
    private View btnPilihMejaDb;

    private EditText etDescription;
    private RecyclerView rvCartItems;
    private TextView tvSubtotal;

    // 3 Tombol Aksi Bawah
    private MaterialButton btnLanjutPembayaran;
    private MaterialButton btnSimpanPesanan;
    private MaterialButton btnTambahPesanan;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);

        CartManager.getInstance().addListener(this);

        setupCollapsibleCart();
        setupCustomerSwitchLogic();
        setupTableSwitchLogic();

        updateCartUI(CartManager.getInstance().getTotalQuantity(), CartManager.getInstance().getTotalPrice());
        setupCartList();

        // Navigasi Pembayaran
        btnLanjutPembayaran.setOnClickListener(v -> {
            if (navigator != null) navigator.goToNextStep();
        });

        // Tombol Tambah Menu (Kembali ke Beranda)
        btnTambahPesanan.setOnClickListener(v -> requireActivity().finish());
        btnPilihMenuDulu.setOnClickListener(v -> requireActivity().finish());

        // Tombol Hold / Simpan
        // Tombol Hold / Simpan
        // Tombol Hold / Simpan
        btnSimpanPesanan.setOnClickListener(v -> {
            String customerName = getSelectedCustomerName();
            if (customerName == null || customerName.isEmpty()) customerName = "UMUM";

            double total = CartManager.getInstance().getTotalPrice();
            int qty = CartManager.getInstance().getTotalQuantity();
            String trx = "TRX-" + (int)(Math.random() * 1000); // Dummy TRX

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE dd-MM-yyyy HH:mm", new java.util.Locale("id", "ID"));
            String currentDate = sdf.format(new java.util.Date());

            // 1. Fotocopy (Clone) detail produk sebelum keranjang dikosongkan
            List<MenuItem> clonedItems = new ArrayList<>();
            for (MenuItem item : CartManager.getInstance().getCartItems()) {
                MenuItem clone = new MenuItem(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getImageResId(), item.getStock());
                clone.setQuantity(item.getQuantity());
                clone.setNote(item.getNote());
                clone.setCustomPrice(item.getCustomPrice());
                clone.setDiscount(item.getDiscount());
                clone.setDiscountPercent(item.isDiscountPercent());
                clonedItems.add(clone);
            }

            // 2. Simpan ke HoldManager beserta clonedItems
            HoldManager.getInstance().addHoldOrder(
                    new HoldManager.HoldOrder(trx, 1, currentDate, customerName, qty, total, clonedItems)
            );

            // 3. Bersihkan Keranjang Utama
            CartManager.getInstance().clear();

            // 4. Pindah ke Halaman Sukses
            android.content.Intent intent = new android.content.Intent(requireActivity(), HoldSuccessActivity.class);
            intent.putExtra("EXTRA_TOTAL", total);
            intent.putExtra("EXTRA_TRX", trx);
            startActivity(intent);
            requireActivity().finish();
        });

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
        CartManager.getInstance().removeListener(this);
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

        btnLanjutPembayaran = view.findViewById(R.id.btnLanjutPembayaran);
        btnSimpanPesanan = view.findViewById(R.id.btnSimpanPesanan);
        btnTambahPesanan = view.findViewById(R.id.btnTambahPesanan);
    }

    @Override
    public void onCartChanged(int totalQuantity, double totalPrice) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            updateCartUI(totalQuantity, totalPrice);
            setupCartList();
        });
    }

    private void updateCartUI(int totalQuantity, double totalPrice) {
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        tvSubtotal.setText(format.format(totalPrice).replace("Rp", "Rp "));

        if (totalQuantity <= 0) {
            llEmptyCart.setVisibility(View.VISIBLE);
            llPriceSummary.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            headerRingkasan.setVisibility(View.GONE);

            btnLanjutPembayaran.setEnabled(false);
            btnLanjutPembayaran.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            btnSimpanPesanan.setEnabled(false);
        } else {
            llEmptyCart.setVisibility(View.GONE);
            llPriceSummary.setVisibility(View.VISIBLE);
            headerRingkasan.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(isCartExpanded ? View.VISIBLE : View.GONE);

            btnLanjutPembayaran.setEnabled(true);
            btnLanjutPembayaran.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
            btnSimpanPesanan.setEnabled(true);
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

    public String getSelectedCustomerName() {
        if (switchCustomer.isChecked()) return etCustomerName.getText().toString().trim();
        return null;
    }

    private void setupCartList() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        List<MenuItem> cartItems = CartManager.getInstance().getCartItems();
        List<CartLineItem> displayItems = new ArrayList<>();

        if (cartItems != null) {
            for (MenuItem item : cartItems) {
                displayItems.add(new CartLineItem(item, "Normal"));
            }
        }

        rvCartItems.setAdapter(new CheckoutCartAdapter(displayItems, new CheckoutCartAdapter.CartActionListener() {
            @Override
            public void onAddQty(MenuItem item) {
                CartManager.getInstance().addOne(item);
            }

            @Override
            public void onMinQty(MenuItem item) {
                CartManager.getInstance().removeOne(item);
            }

            @Override
            public void onRemove(MenuItem item) {
                CartManager.getInstance().setQuantity(item, 0);
            }

            @Override
            public void onEditNote(MenuItem item) {
                com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_note, null);
                dialog.setContentView(view);

                EditText etNote = view.findViewById(R.id.etNote);
                etNote.setText(item.getNote());

                view.findViewById(R.id.btnSimpan).setOnClickListener(v -> {
                    item.setNote(etNote.getText().toString().trim());
                    CartManager.getInstance().setQuantity(item, item.getQuantity());
                    dialog.dismiss();
                });
                dialog.show();
            }

            @Override
            public void onEditPrice(MenuItem item) {
                com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_price, null);
                dialog.setContentView(view);

                TextView tvName = view.findViewById(R.id.tvItemName);
                EditText etPrice = view.findViewById(R.id.etCustomPrice);
                tvName.setText(item.getName());
                etPrice.setText(String.format(java.util.Locale.US, "%.0f", item.getCustomPrice()));

                view.findViewById(R.id.btnSimpan).setOnClickListener(v -> {
                    try {
                        double newPrice = Double.parseDouble(etPrice.getText().toString());
                        item.setCustomPrice(newPrice);
                        CartManager.getInstance().setQuantity(item, item.getQuantity());
                    } catch (NumberFormatException e) { }
                    dialog.dismiss();
                });
                dialog.show();
            }

            @Override
            public void onEditDiscount(MenuItem item) {
                com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_discount, null);
                dialog.setContentView(view);

                TextView tvName = view.findViewById(R.id.tvItemName);
                EditText etVal = view.findViewById(R.id.etDiscountValue);
                TextView tvPrefix = view.findViewById(R.id.tvPrefix);
                com.google.android.material.button.MaterialButtonToggleGroup toggle = view.findViewById(R.id.toggleDiscountType);

                TextView tvBase = view.findViewById(R.id.tvSummaryBase);
                TextView tvDisc = view.findViewById(R.id.tvSummaryDisc);
                TextView tvFinal = view.findViewById(R.id.tvSummaryFinal);

                tvName.setText(item.getName());
                java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
                format.setMaximumFractionDigits(0);

                if (item.isDiscountPercent()) {
                    toggle.check(R.id.btnPercent);
                    tvPrefix.setText("% ");
                } else {
                    toggle.check(R.id.btnNominal);
                    tvPrefix.setText("Rp ");
                }
                if (item.getDiscount() > 0) {
                    etVal.setText(String.format(java.util.Locale.US, "%.0f", item.getDiscount()));
                }

                Runnable updateSummary = () -> {
                    double base = item.getCustomPrice();
                    tvBase.setText(format.format(base).replace("Rp", ""));

                    double discVal = 0;
                    try { discVal = Double.parseDouble(etVal.getText().toString()); } catch (Exception e) {}

                    boolean isPercent = toggle.getCheckedButtonId() == R.id.btnPercent;
                    double potong = isPercent ? (base * (discVal / 100)) : discVal;

                    tvDisc.setText("- " + format.format(potong).replace("Rp", ""));
                    tvFinal.setText(format.format(base - potong).replace("Rp", ""));
                };

                updateSummary.run();

                toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    if (isChecked) {
                        if (checkedId == R.id.btnPercent) {
                            tvPrefix.setText("% ");
                        } else {
                            tvPrefix.setText("Rp ");
                        }
                        updateSummary.run();
                    }
                });

                etVal.addTextChangedListener(new android.text.TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    public void onTextChanged(CharSequence s, int start, int before, int count) { updateSummary.run(); }
                    public void afterTextChanged(android.text.Editable s) {}
                });

                view.findViewById(R.id.btnSimpan).setOnClickListener(v -> {
                    try {
                        item.setDiscount(Double.parseDouble(etVal.getText().toString()));
                        item.setDiscountPercent(toggle.getCheckedButtonId() == R.id.btnPercent);
                        CartManager.getInstance().setQuantity(item, item.getQuantity());
                    } catch (Exception e) {
                        item.setDiscount(0);
                        item.setDiscountPercent(false);
                        CartManager.getInstance().setQuantity(item, item.getQuantity());
                    }
                    dialog.dismiss();
                });
                dialog.show();
            }
        }));
    }
}