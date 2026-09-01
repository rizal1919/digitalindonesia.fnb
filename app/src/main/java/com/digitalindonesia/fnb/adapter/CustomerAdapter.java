package com.digitalindonesia.fnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    public interface OnCustomerClickListener {
        void onCustomerClicked(Customer customer);
    }

    private final List<Customer> customerList;
    private final OnCustomerClickListener listener;

    public CustomerAdapter(List<Customer> customerList, OnCustomerClickListener listener) {
        this.customerList = customerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.tvCustomerCode.setText(customer.getCustomerCode());
        holder.tvCustomerName.setText(customer.getFullName());
        holder.tvCustomerPhone.setText(customer.getPhone());

        // Ambil inisial huruf pertama (misal: "Kasman" jadi "K")
        if (customer.getFullName() != null && !customer.getFullName().isEmpty()) {
            holder.tvInitial.setText(customer.getFullName().substring(0, 1).toUpperCase());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCustomerClicked(customer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvCustomerCode, tvCustomerName, tvCustomerPhone;

        ViewHolder(View view) {
            super(view);
            tvInitial = view.findViewById(R.id.tvInitial);
            tvCustomerCode = view.findViewById(R.id.tvCustomerCode);
            tvCustomerName = view.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = view.findViewById(R.id.tvCustomerPhone);
        }
    }
}