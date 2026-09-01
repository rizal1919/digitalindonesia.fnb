package com.digitalindonesia.fnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.model.TableItem;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    public interface OnTableClickListener {
        void onTableClicked(TableItem table);
    }

    private final List<TableItem> tableList;
    private final OnTableClickListener listener;

    public TableAdapter(List<TableItem> tableList, OnTableClickListener listener) {
        this.tableList = tableList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableItem table = tableList.get(position);
        holder.tvTableName.setText(table.getTableName());
        holder.tvTableDetails.setText("Kapasitas: " + table.getCapacity() + " | Area: " + table.getArea());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTableClicked(table);
        });
    }

    @Override
    public int getItemCount() { return tableList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableName, tvTableDetails;
        ViewHolder(View view) {
            super(view);
            tvTableName = view.findViewById(R.id.tvTableName);
            tvTableDetails = view.findViewById(R.id.tvTableDetails);
        }
    }
}