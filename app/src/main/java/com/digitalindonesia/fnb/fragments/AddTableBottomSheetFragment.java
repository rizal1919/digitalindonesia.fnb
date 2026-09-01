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

public class AddTableBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "AddTableBottomSheet";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_table_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnSimpanMeja).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Meja berhasil disimpan!", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}