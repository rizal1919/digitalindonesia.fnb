package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digitalindonesia.fnb.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class AddCustomerBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "AddCustomerBottomSheet";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_customer_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButtonToggleGroup toggleGender = view.findViewById(R.id.toggleGender);
        MaterialButton btnPria = view.findViewById(R.id.btnPria);
        MaterialButton btnWanita = view.findViewById(R.id.btnWanita);

        // Logika warna untuk toggle gender
        toggleGender.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnPria) {
                    btnPria.setTextColor(android.graphics.Color.parseColor("#00796B"));
                    btnPria.setStrokeColorResource(R.color.navy_end); // Sesuaikan dengan warna hex kamu

                    btnWanita.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                    btnWanita.setStrokeColorResource(android.R.color.darker_gray);
                } else if (checkedId == R.id.btnWanita) {
                    btnWanita.setTextColor(android.graphics.Color.parseColor("#00796B"));
                    btnWanita.setStrokeColorResource(R.color.navy_end);

                    btnPria.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                    btnPria.setStrokeColorResource(android.R.color.darker_gray);
                }
            }
        });

        // Trigger Import Kontak
        view.findViewById(R.id.btnImportContact).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur import kontak menyusul", Toast.LENGTH_SHORT).show();
        });

        // Simpan Data
        view.findViewById(R.id.btnSimpanPelanggan).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pelanggan disimpan!", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}