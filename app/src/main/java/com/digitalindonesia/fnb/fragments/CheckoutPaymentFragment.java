package com.digitalindonesia.fnb.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitalindonesia.fnb.CheckoutStepNavigator;
import com.digitalindonesia.fnb.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class CheckoutPaymentFragment extends Fragment {

    private CheckoutStepNavigator navigator;
    private TextView tvPrefixDiskon, tvSuffixDiskon;
    private MaterialButtonToggleGroup toggleDiscTrx;

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
        return inflater.inflate(R.layout.fragment_checkout_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Setup Diskon Transaksi (Toggle Rp / %) ---
        tvPrefixDiskon = view.findViewById(R.id.tvPrefixDiskon);
        tvSuffixDiskon = view.findViewById(R.id.tvSuffixDiskon);
        toggleDiscTrx = view.findViewById(R.id.toggleDiscTrx);

        // Default State (Persen)
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
            }
        });

        // --- Setup Metode Pembayaran & Jenis Pembayaran (Styling otomatis) ---
        setupPaymentToggle(view.findViewById(R.id.togglePaymentType));
        setupPaymentToggle(view.findViewById(R.id.togglePaymentMethod));

        view.findViewById(R.id.btnAddCharge).setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog masterDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
            View masterView = getLayoutInflater().inflate(R.layout.dialog_master_biaya, null);
            masterDialog.setContentView(masterView);

            masterView.findViewById(R.id.btnCloseMaster).setOnClickListener(v1 -> masterDialog.dismiss());

            // Aksi Jika Memilih Item "Renov" di Master
            masterView.findViewById(R.id.btnPilihRenov).setOnClickListener(v1 -> {
                addChargeToUI("Renov", 1500);
                masterDialog.dismiss();
            });

            // Aksi Jika Memilih Item "Kepake" di Master
            masterView.findViewById(R.id.btnPilihKepake).setOnClickListener(v1 -> {
                addChargeToUI("Kepake", 1000);
                masterDialog.dismiss();
            });

            // Aksi Buka Form "Tambah Biaya Baru"
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

        // --- Aksi Tombol Tambahan ---
        view.findViewById(R.id.btnTaxSetting).setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog taxDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
            View taxView = getLayoutInflater().inflate(R.layout.dialog_tax_settings, null);
            taxDialog.setContentView(taxView);

            taxView.findViewById(R.id.btnCloseTax).setOnClickListener(v1 -> taxDialog.dismiss());

            taxView.findViewById(R.id.btnApplyTax).setOnClickListener(v1 -> {
                EditText etTax = taxView.findViewById(R.id.etTaxValue);
                String taxStr = etTax.getText().toString();
                if (!taxStr.isEmpty() && !taxStr.equals("0")) {
                    addTaxToUI(Integer.parseInt(taxStr));
                    taxDialog.dismiss();
                }
            });
            taxDialog.show();
        });

        // Fitur Lainnya Tetap
        view.findViewById(R.id.btnSplitBill).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Split Bill dipanggil", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnDatePick).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pilih Tanggal Transaksi", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.chipExact).setOnClickListener(v -> {
            EditText etAmount = view.findViewById(R.id.etAmountReceived);
            etAmount.setText("36000");
        });

        MaterialButton btnProses = view.findViewById(R.id.btnProsesPembayaran);
        btnProses.setOnClickListener(v -> {
            if (navigator != null) {
                navigator.goToNextStep();
            }
        });
    }

    // --- Helper Method untuk Menambahkan UI Dinamis ---

    private void addChargeToUI(String name, double price) {
        android.widget.LinearLayout container = getView().findViewById(R.id.llChargeContainer);
        View chargeView = getLayoutInflater().inflate(R.layout.item_added_charge, null);

        TextView tvName = chargeView.findViewById(R.id.tvChargeName);
        TextView tvPrice = chargeView.findViewById(R.id.tvChargePrice);

        tvName.setText(name);
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        tvPrice.setText(format.format(price).replace("Rp", "Rp "));

        chargeView.findViewById(R.id.btnRemoveCharge).setOnClickListener(v -> {
            container.removeView(chargeView);
            // TODO: Kalkulasi ulang Grand Total
        });

        container.addView(chargeView);
        // TODO: Kalkulasi ulang Grand Total
    }

    private void addTaxToUI(int percent) {
        android.widget.LinearLayout container = getView().findViewById(R.id.llTaxContainer);
        container.removeAllViews(); // Maksimal 1 pajak saja

        View taxView = getLayoutInflater().inflate(R.layout.item_added_tax, null);
        TextView tvName = taxView.findViewById(R.id.tvTaxName);
        TextView tvPrice = taxView.findViewById(R.id.tvTaxPrice);

        tvName.setText("PPN (" + percent + "%)");
        // Sementara nominal pajak dummy, nanti dikalkulasi dari Grand Total
        tvPrice.setText("Rp 250");

        taxView.findViewById(R.id.btnRemoveTax).setOnClickListener(v -> {
            container.removeView(taxView);
            // TODO: Kalkulasi ulang Grand Total
        });

        container.addView(taxView);
        // TODO: Kalkulasi ulang Grand Total
    }

    // Fungsi otomatis ubah warna toggle button saat diklik
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
            btn.setStrokeColorResource(R.color.bg_soft); // abu-abu terang
        }
    }
}