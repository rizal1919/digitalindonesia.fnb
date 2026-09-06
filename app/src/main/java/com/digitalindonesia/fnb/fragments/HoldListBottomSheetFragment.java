package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.HoldManager;
import com.digitalindonesia.fnb.model.HoldManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.NumberFormat;
import java.util.Locale;

public class HoldListBottomSheetFragment extends BottomSheetDialogFragment {

    // === 1. Buat Listener Penghubung ===
    public interface OnRestoreListener {
        void onRestore(HoldManager.HoldOrder order, boolean directToPay);
    }

    private Runnable onDismissListener;
    private OnRestoreListener restoreListener;

    public void setOnDismissListener(Runnable listener) { this.onDismissListener = listener; }
    public void setOnRestoreListener(OnRestoreListener listener) { this.restoreListener = listener; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hold_list_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnNewOrder).setOnClickListener(v -> dismiss());

        RecyclerView rv = view.findViewById(R.id.rvHoldOrders);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new HoldAdapter());
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) onDismissListener.run();
    }

    // === 2. Inner Adapter Hold List ===
    private class HoldAdapter extends RecyclerView.Adapter<HoldAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hold_older, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HoldManager.HoldOrder order = HoldManager.getInstance().getHoldOrders().get(position);

            holder.tvQueue.setText("Antrean #" + order.queueNumber);
            holder.tvTrx.setText(order.trxNumber);
            holder.tvDate.setText(order.date);
            holder.tvCustomer.setText(order.customerName);
            holder.tvQty.setText(order.totalItems + " Produk");

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            format.setMaximumFractionDigits(0);
            holder.tvPrice.setText(format.format(order.totalPrice).replace("Rp", "Rp "));

            holder.btnCancel.setOnClickListener(v -> {
                HoldManager.getInstance().removeHoldOrder(order);
                notifyDataSetChanged();
                android.widget.Toast.makeText(getContext(), "Pesanan dihapus", android.widget.Toast.LENGTH_SHORT).show();
            });

            // Lempar aksi ke MenuListFragment
            holder.btnAdd.setOnClickListener(v -> {
                if (restoreListener != null) restoreListener.onRestore(order, false);
                dismiss();
            });

            // Lempar aksi ke MenuListFragment dengan flag directToPay = true
            holder.btnPay.setOnClickListener(v -> {
                if (restoreListener != null) restoreListener.onRestore(order, true);
                dismiss();
            });
        }

        @Override
        public int getItemCount() { return HoldManager.getInstance().getHoldOrders().size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQueue, tvTrx, tvDate, tvCustomer, tvQty, tvPrice;
            View btnCancel, btnAdd, btnPay;
            ViewHolder(View v) {
                super(v);
                tvQueue = v.findViewById(R.id.tvQueue);
                tvTrx = v.findViewById(R.id.tvTrx);
                tvDate = v.findViewById(R.id.tvDate);
                tvCustomer = v.findViewById(R.id.tvCustomer);
                tvQty = v.findViewById(R.id.tvQty);
                tvPrice = v.findViewById(R.id.tvPrice);
                btnCancel = v.findViewById(R.id.btnCancel);
                btnAdd = v.findViewById(R.id.btnAdd);
                btnPay = v.findViewById(R.id.btnPay);
            }
        }
    }
}