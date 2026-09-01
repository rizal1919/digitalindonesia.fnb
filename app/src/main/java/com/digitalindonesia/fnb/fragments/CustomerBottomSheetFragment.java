package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.adapter.CustomerAdapter;
import com.digitalindonesia.fnb.model.Customer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomerBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "CustomerBottomSheetFragment";

    public interface CustomerSelectionListener {
        void onCustomerSelected(Customer customer);
    }

    private CustomerSelectionListener listener;
    private CustomerAdapter adapter;
    private final List<Customer> fullList = new ArrayList<>();
    private final List<Customer> filteredList = new ArrayList<>();

    public void setCustomerSelectionListener(CustomerSelectionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        EditText etSearchCustomer = view.findViewById(R.id.etSearchCustomer);
        RecyclerView rvCustomers = view.findViewById(R.id.rvCustomers);
        View btnAddCustomer = view.findViewById(R.id.btnAddCustomer);

        btnClose.setOnClickListener(v -> dismiss());

        btnAddCustomer.setOnClickListener(v -> {
            // Panggil fungsi untuk menampilkan dialog form tambah pelanggan
            AddCustomerBottomSheetFragment formSheet = new AddCustomerBottomSheetFragment();
            formSheet.show(getParentFragmentManager(), AddCustomerBottomSheetFragment.TAG);
        });

        setupDummyData();

        adapter = new CustomerAdapter(filteredList, customer -> {
            if (listener != null) {
                listener.onCustomerSelected(customer);
            }
            dismiss(); // Tutup bottom sheet setelah dipilih
        });

        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(adapter);

        etSearchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Customer item : fullList) {
                if (item.getFullName().toLowerCase().contains(text) || item.getCustomerCode().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupDummyData() {
        fullList.add(new Customer(1, "PLG1", "Kasman", "087576554", "kasman@mail.com", "Laki-laki", "Sby", "1990-01-01", null));
        fullList.add(new Customer(2, "PLG2-1", "Sarmuji", "081234567", "sarmuji@mail.com", "Laki-laki", "Sda", "1992-05-12", null));
        fullList.add(new Customer(3, "PLG3", "Aldi", "089876543", "aldi@mail.com", "Laki-laki", "Sby", "1995-08-20", null));
        fullList.add(new Customer(4, "PLG4", "Rizal Fathurrahman", "085612345", "rizal@mail.com", "Laki-laki", "Sby", "1999-10-15", null));

        filteredList.addAll(fullList);
    }
}