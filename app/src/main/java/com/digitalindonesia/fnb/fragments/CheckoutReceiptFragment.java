package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digitalindonesia.fnb.R;
import com.google.android.material.button.MaterialButton;

public class CheckoutReceiptFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnKembaliBeranda = view.findViewById(R.id.btnKembaliBeranda);
        MaterialButton btnCetakStruk = view.findViewById(R.id.btnCetakStruk);

        btnKembaliBeranda.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish(); // Kembali ke MenuListFragment / MainActivity
            }
        });

        btnCetakStruk.setOnClickListener(v -> {
            // TODO: kirim data struk ke printer thermal (Bluetooth/USB) sesuai SDK printer yang dipakai
        });
    }
}
