package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitalindonesia.fnb.CheckoutStepNavigator;
import com.digitalindonesia.fnb.R;
import com.google.android.material.button.MaterialButton;

public class CheckoutPaymentFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_checkout_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnProses = view.findViewById(R.id.btnProsesPembayaran);
        btnProses.setOnClickListener(v -> {
            // TODO: validasi metode pembayaran terpilih (rgPaymentMethod.getCheckedRadioButtonId())
            // TODO: proses transaksi pembayaran sesuai metode
            if (navigator != null) {
                navigator.goToNextStep();
            }
        });
    }
}
