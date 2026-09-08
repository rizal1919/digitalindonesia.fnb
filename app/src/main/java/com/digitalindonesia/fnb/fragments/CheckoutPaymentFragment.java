package com.digitalindonesia.fnb.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.digitalindonesia.fnb.CheckoutStepNavigator;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.cart.CartManager;
import com.digitalindonesia.fnb.model.MenuItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CheckoutPaymentFragment extends Fragment {

    private CheckoutStepNavigator navigator;
    private TextView tvPrefixDiskon, tvSuffixDiskon;
    private EditText etDiskonTrx, etAmountReceived;
    private MaterialButtonToggleGroup toggleDiscTrx;
    private boolean isPajakSebelumDiskon = false; // Default sesudah diskon
    // UI Summary
    private TextView tvSummarySubtotal, tvSummaryItemDisc, tvSummaryTrxDisc;
    private TextView tvSummaryTax, tvSummaryCharge, tvSummaryGrandTotal, tvBottomTotal;
    private TextView tvEmptyChargeTax;
    private TextView tvTransactionDate;

    private LinearLayout llChargeContainer, llTaxContainer;
    private TextView tvTaxPriceInList; // Referensi untuk update harga pajak real-time di UI

    // Variabel state kalkulasi
    private double totalBiayaTambahan = 0;
    private double pajakPercent = 0;
    private boolean isPajakTermasukBiaya = true;
    private Calendar selectedDate = Calendar.getInstance();
    private double currentGrandTotal = 0;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof CheckoutStepNavigator) navigator = (CheckoutStepNavigator) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding UI
        tvEmptyChargeTax = view.findViewById(R.id.tvEmptyChargeTax);
        llChargeContainer = view.findViewById(R.id.llChargeContainer);
        llTaxContainer = view.findViewById(R.id.llTaxContainer);

        tvSummarySubtotal = view.findViewById(R.id.tvSummarySubtotal);
        tvSummaryItemDisc = view.findViewById(R.id.tvSummaryItemDisc);
        tvSummaryTrxDisc = view.findViewById(R.id.tvSummaryTrxDisc);
        tvSummaryTax = view.findViewById(R.id.tvSummaryTax);
        tvSummaryCharge = view.findViewById(R.id.tvSummaryCharge);
        tvSummaryGrandTotal = view.findViewById(R.id.tvSummaryGrandTotal);
        tvBottomTotal = view.findViewById(R.id.tvBottomTotal);

        tvTransactionDate = view.findViewById(R.id.tvTransactionDate);
        etAmountReceived = view.findViewById(R.id.etAmountReceived);

        tvPrefixDiskon = view.findViewById(R.id.tvPrefixDiskon);
        tvSuffixDiskon = view.findViewById(R.id.tvSuffixDiskon);
        etDiskonTrx = view.findViewById(R.id.etDiskonTrx);
        toggleDiscTrx = view.findViewById(R.id.toggleDiscTrx);

        // Set Tanggal Default (Saat ini)
        updateDateText();

        // Setup Diskon Transaksi (Toggle Rp / %)
        toggleDiscTrx.check(R.id.btnDiscPercent);
        tvPrefixDiskon.setText("");
        tvSuffixDiskon.setText("%");

        toggleDiscTrx.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                MaterialButton btnRp = view.findViewById(R.id.btnDiscRp);
                MaterialButton btnPercent = view.findViewById(R.id.btnDiscPercent);

                if (checkedId == R.id.btnDiscRp) {
                    tvPrefixDiskon.setText("Rp ");
                    tvSuffixDiskon.setText("");
                    setToggleColor(btnRp, true);
                    setToggleColor(btnPercent, false);
                } else {
                    tvPrefixDiskon.setText("");
                    tvSuffixDiskon.setText("%");
                    setToggleColor(btnPercent, true);
                    setToggleColor(btnRp, false);
                }
                calculateGrandTotal(); // Kalkulasi ulang saat tipe diskon ganti
            }
        });

        // Trigger kalkulasi saat input nominal diskon diketik
        etDiskonTrx.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateGrandTotal(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Setup Styling Toggle Lainnya
        setupPaymentToggle(view.findViewById(R.id.togglePaymentType));
        setupPaymentToggle(view.findViewById(R.id.togglePaymentMethod));

        // --- Aksi Tombol Bawah ---

        // 1. DATE PICKER
        view.findViewById(R.id.btnDatePick).setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (v1, y, m, d) -> {
                selectedDate.set(Calendar.YEAR, y);
                selectedDate.set(Calendar.MONTH, m);
                selectedDate.set(Calendar.DAY_OF_MONTH, d);

                // Setelah milih tanggal, lanjut milih jam
                new TimePickerDialog(getContext(), (v2, h, mn) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, h);
                    selectedDate.set(Calendar.MINUTE, mn);
                    updateDateText();
                }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();

            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 2. BIAYA TAMBAHAN
        view.findViewById(R.id.btnAddCharge).setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog masterDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
            View masterView = getLayoutInflater().inflate(R.layout.dialog_master_biaya, null);
            masterDialog.setContentView(masterView);

            masterView.findViewById(R.id.btnCloseMaster).setOnClickListener(v1 -> masterDialog.dismiss());

            masterView.findViewById(R.id.btnPilihRenov).setOnClickListener(v1 -> {
                addChargeToUI("Renov", 1500);
                masterDialog.dismiss();
            });

            masterView.findViewById(R.id.btnPilihKepake).setOnClickListener(v1 -> {
                addChargeToUI("Kepake", 1000);
                masterDialog.dismiss();
            });

            masterView.findViewById(R.id.btnTambahBiayaBaru).setOnClickListener(v1 -> {
                masterDialog.dismiss();
                com.google.android.material.bottomsheet.BottomSheetDialog addDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
                View addView = getLayoutInflater().inflate(R.layout.dialog_add_biaya_baru, null);
                addDialog.setContentView(addView);

                addView.findViewById(R.id.btnCloseAdd).setOnClickListener(v2 -> addDialog.dismiss());

                addView.findViewById(R.id.btnSimpanBiayaBaru).setOnClickListener(v2 -> {
                    EditText etName = addView.findViewById(R.id.etNamaBiayaBaru);
                    EditText etPrice = addView.findViewById(R.id.etHargaBiayaBaru);
                    String name = etName.getText().toString();
                    String priceStr = etPrice.getText().toString();

                    if (!name.isEmpty() && !priceStr.isEmpty()) {
                        addChargeToUI(name, Double.parseDouble(priceStr));
                        addDialog.dismiss();
                    }
                });
                addDialog.show();
            });

            masterDialog.show();
        });

        // 3. PENGATURAN PAJAK
        view.findViewById(R.id.btnTaxSetting).setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog taxDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
            View taxView = getLayoutInflater().inflate(R.layout.dialog_tax_settings, null);
            taxDialog.setContentView(taxView);

            taxView.findViewById(R.id.btnCloseTax).setOnClickListener(v1 -> taxDialog.dismiss());

            // Setup Dropdown Spinner
            android.widget.Spinner spinnerLogic = taxView.findViewById(R.id.spinnerTaxLogic);
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    new String[]{"Sesudah Diskon", "Sebelum Diskon"}
            );
            spinnerLogic.setAdapter(adapter);
            spinnerLogic.setSelection(isPajakSebelumDiskon ? 1 : 0);

            taxView.findViewById(R.id.btnApplyTax).setOnClickListener(v1 -> {
                EditText etTax = taxView.findViewById(R.id.etTaxValue);
                SwitchCompat switchTax = taxView.findViewById(R.id.switchTaxAddon);

                String taxStr = etTax.getText().toString();
                if (!taxStr.isEmpty() && !taxStr.equals("0")) {
                    pajakPercent = Double.parseDouble(taxStr);
                    isPajakTermasukBiaya = switchTax.isChecked();

                    // Baca pilihan dari dropdown
                    isPajakSebelumDiskon = spinnerLogic.getSelectedItemPosition() == 1;

                    addTaxToUI((int)pajakPercent, isPajakTermasukBiaya);
                    taxDialog.dismiss();
                } else {
                    llTaxContainer.removeAllViews();
                    pajakPercent = 0;
                    checkEmptyState();
                    calculateGrandTotal();
                    taxDialog.dismiss();
                }
            });
            taxDialog.show();
        });

        // 4. CHIP UANG PAS
        view.findViewById(R.id.chipExact).setOnClickListener(v -> {
            // Set amount received sama dengan tagihan
            etAmountReceived.setText(String.format(Locale.US, "%.0f", currentGrandTotal));
        });

        view.findViewById(R.id.btnProsesPembayaran).setOnClickListener(v -> {
            if (navigator != null) navigator.goToNextStep();
        });

        view.findViewById(R.id.btnSplitBill).setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog splitDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
            View splitView = getLayoutInflater().inflate(R.layout.dialog_split_bill, null);
            splitDialog.setContentView(splitView);

            // Set Total Tagihan di Header Dialog
            TextView tvTotalSplit = splitView.findViewById(R.id.tvTotalSplit);
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
            format.setMaximumFractionDigits(0);
            if (tvTotalSplit != null) {
                tvTotalSplit.setText("Total Tagihan: " + format.format(currentGrandTotal).replace("Rp", "Rp "));
            }

            // 1. Aksi Klik Split Per Item
            View btnSplitItem = splitView.findViewById(R.id.btnSplitBill); // Pastikan LinearLayout Split Per Item di dialog_split_bill.xml diberi id ini
            if (btnSplitItem == null) {
                // Jika kamu belum pasang ID di XML, kita ambil index child-nya (baris pertama)
                android.widget.LinearLayout rootLayout = (android.widget.LinearLayout) splitView;
                btnSplitItem = rootLayout.getChildAt(2); // Baris Split Per Item
            }

            btnSplitItem.setOnClickListener(v1 -> {
                splitDialog.dismiss();

                // Panggil Fragment Pintar Split Bill
                SplitBillPerItemFragment fragment = new SplitBillPerItemFragment();
                fragment.show(getParentFragmentManager(), "SplitBillPerItem");
            });

            splitDialog.show();
        });

        // Hitung Data Awal (dari Cart)
        calculateGrandTotal();
    }

    // ================= HELPER & LOGIC =================

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm", new Locale("id", "ID"));
        tvTransactionDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void checkEmptyState() {
        if (llChargeContainer.getChildCount() == 0 && llTaxContainer.getChildCount() == 0) {
            tvEmptyChargeTax.setVisibility(View.VISIBLE);
        } else {
            tvEmptyChargeTax.setVisibility(View.GONE);
        }
    }

    private void addChargeToUI(String name, double price) {
        View chargeView = getLayoutInflater().inflate(R.layout.item_added_charge, null);

        TextView tvName = chargeView.findViewById(R.id.tvChargeName);
        TextView tvPrice = chargeView.findViewById(R.id.tvChargePrice);

        tvName.setText(name);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        tvPrice.setText(format.format(price).replace("Rp", "Rp "));

        chargeView.findViewById(R.id.btnRemoveCharge).setOnClickListener(v -> {
            llChargeContainer.removeView(chargeView);
            totalBiayaTambahan -= price;
            checkEmptyState();
            calculateGrandTotal();
        });

        llChargeContainer.addView(chargeView);
        totalBiayaTambahan += price;

        checkEmptyState();
        calculateGrandTotal();
    }

    private void addTaxToUI(int percent, boolean isIncludingCharge) {
        llTaxContainer.removeAllViews(); // Hanya boleh ada 1 card pajak

        View taxView = getLayoutInflater().inflate(R.layout.item_added_tax, null);
        TextView tvName = taxView.findViewById(R.id.tvTaxName);
        TextView tvDesc = taxView.findViewById(R.id.tvTaxDesc);
        tvTaxPriceInList = taxView.findViewById(R.id.tvTaxPrice); // Simpan global agar harganya bisa diupdate realtime

        tvName.setText("PPN (" + percent + "%)");
        tvDesc.setText(isIncludingCharge ? "(Termasuk hitung biaya tambahan)" : "(Sebelum hitung biaya tambahan)");

        taxView.findViewById(R.id.btnRemoveTax).setOnClickListener(v -> {
            llTaxContainer.removeView(taxView);
            pajakPercent = 0;
            tvTaxPriceInList = null;
            checkEmptyState();
            calculateGrandTotal();
        });

        llTaxContainer.addView(taxView);
        checkEmptyState();
        calculateGrandTotal();
    }

    // OTAK KALKULASI UTAMA
    private void calculateGrandTotal() {
        double subtotalItem = 0;
        double diskonItem = 0;

        // 1. Tarik dari Keranjang
        for (MenuItem item : CartManager.getInstance().getCartItems()) {
            double basePrice = item.getCustomPrice() * item.getQuantity();
            double finalPrice = item.getTotalPriceForQuantity(); // Harga setelah diskon per item
            subtotalItem += basePrice;
            diskonItem += (basePrice - finalPrice);
        }

        // 2. Diskon Transaksi
        double diskonTrxValue = 0;
        String diskonInput = etDiskonTrx.getText().toString();
        if (!diskonInput.isEmpty()) {
            try {
                double val = Double.parseDouble(diskonInput);
                if (toggleDiscTrx.getCheckedButtonId() == R.id.btnDiscPercent) {
                    diskonTrxValue = (subtotalItem - diskonItem) * (val / 100.0);
                } else {
                    diskonTrxValue = val;
                }
            } catch (Exception e) {}
        }

        double dpp = (subtotalItem - diskonItem) - diskonTrxValue;
        if (dpp < 0) dpp = 0; // Cegah minus

        // 3. Pajak
        double pajakValue = 0;
        if (pajakPercent > 0) {
            // Tentukan Dasar Pengenaan Pajak (DPP)
            double dppPajak;
            if (isPajakSebelumDiskon) {
                // Pajak dihitung dari harga asli (hanya dikurangi diskon item jika ada)
                dppPajak = subtotalItem - diskonItem;
            } else {
                // Pajak dihitung dari harga setelah semua diskon dipotong
                dppPajak = dpp;
            }

            // Tambahkan biaya layanan ke dalam perhitungan pajak jika toggle aktif
            if (isPajakTermasukBiaya) {
                dppPajak += totalBiayaTambahan;
            }

            pajakValue = dppPajak * (pajakPercent / 100.0);
        }

        // 4. Grand Total
        currentGrandTotal = dpp + totalBiayaTambahan + pajakValue;

        // ================= UPDATE TAMPILAN =================
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);

        tvSummarySubtotal.setText(format.format(subtotalItem).replace("Rp", "Rp "));
        tvSummaryItemDisc.setText("- " + format.format(diskonItem).replace("Rp", "Rp "));
        tvSummaryTrxDisc.setText("- " + format.format(diskonTrxValue).replace("Rp", "Rp "));
        tvSummaryTax.setText(format.format(pajakValue).replace("Rp", "Rp "));
        tvSummaryCharge.setText(format.format(totalBiayaTambahan).replace("Rp", "Rp "));

        tvSummaryGrandTotal.setText(format.format(currentGrandTotal).replace("Rp", "Rp "));
        tvBottomTotal.setText(format.format(currentGrandTotal).replace("Rp", "Rp "));

        // Update label uang pas
        // chipExact.setText("Uang Pas\n" + format.format(currentGrandTotal).replace("Rp", "Rp "));

        // Update harga pajak di list (jika komponennya ada)
        if (tvTaxPriceInList != null) {
            tvTaxPriceInList.setText(format.format(pajakValue).replace("Rp", "Rp "));
        }
    }

    private void setupPaymentToggle(MaterialButtonToggleGroup group) {
        group.addOnButtonCheckedListener((group1, checkedId, isChecked) -> {
            if (isChecked) {
                for (int i = 0; i < group1.getChildCount(); i++) {
                    MaterialButton btn = (MaterialButton) group1.getChildAt(i);
                    setToggleColor(btn, btn.getId() == checkedId);
                }
            }
        });
    }

    private void setToggleColor(MaterialButton btn, boolean isActive) {
        if (isActive) {
            btn.setTextColor(Color.WHITE);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00796B")));
            btn.setStrokeColorResource(android.R.color.transparent);
        } else {
            btn.setTextColor(Color.parseColor("#757575"));
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btn.setStrokeColorResource(R.color.badge_gray_bg);
        }
    }
}