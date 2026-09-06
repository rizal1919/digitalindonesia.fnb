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

        // --- Aksi Tombol Tambahan ---
        view.findViewById(R.id.btnTaxSetting).setOnClickListener(v -> {
            // TODO: Buka BottomSheet Pengaturan Pajak (dialog_tax_settings.xml)
            Toast.makeText(getContext(), "Pengaturan Pajak dipanggil", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnSplitBill).setOnClickListener(v -> {
            // TODO: Buka BottomSheet Split Bill (dialog_split_bill.xml)
            Toast.makeText(getContext(), "Fitur Split Bill dipanggil", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnDatePick).setOnClickListener(v -> {
            // TODO: Buka DatePicker bawaan Android
            Toast.makeText(getContext(), "Pilih Tanggal Transaksi", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.chipExact).setOnClickListener(v -> {
            EditText etAmount = view.findViewById(R.id.etAmountReceived);
            // Ambil nominal dari TextView Total
            etAmount.setText("36000");
        });

        MaterialButton btnProses = view.findViewById(R.id.btnProsesPembayaran);
        btnProses.setOnClickListener(v -> {
            if (navigator != null) {
                navigator.goToNextStep();
            }
        });
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