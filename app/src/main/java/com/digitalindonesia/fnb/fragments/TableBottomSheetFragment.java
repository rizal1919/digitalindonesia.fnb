package com.digitalindonesia.fnb.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.adapter.TableAdapter;
import com.digitalindonesia.fnb.model.TableItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TableBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "TableBottomSheet";

    public interface TableSelectionListener {
        void onTableSelected(TableItem table);
    }

    private TableSelectionListener listener;
    private TableAdapter adapter;
    private final List<TableItem> fullList = new ArrayList<>();
    private final List<TableItem> filteredList = new ArrayList<>();

    public void setTableSelectionListener(TableSelectionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnCloseTable).setOnClickListener(v -> dismiss());
        EditText etSearchTable = view.findViewById(R.id.etSearchTable);
        RecyclerView rvTables = view.findViewById(R.id.rvTables);
        View btnAddTable = view.findViewById(R.id.btnAddTable);

        // Dummy Data
        fullList.add(new TableItem(1, "Meja 01", 4, "Indoor", true));
        fullList.add(new TableItem(2, "Meja 02", 2, "Outdoor", true));
        fullList.add(new TableItem(3, "VIP-A", 8, "Lantai 2", true));
        filteredList.addAll(fullList);

        adapter = new TableAdapter(filteredList, table -> {
            if (listener != null) listener.onTableSelected(table);
            dismiss();
        });

        rvTables.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTables.setAdapter(adapter);

        etSearchTable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Logika memunculkan dialog tambah meja
        btnAddTable.setOnClickListener(v -> {
            AddTableBottomSheetFragment formSheet = new AddTableBottomSheetFragment();
            formSheet.show(getParentFragmentManager(), AddTableBottomSheetFragment.TAG);
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) filteredList.addAll(fullList);
        else {
            text = text.toLowerCase();
            for (TableItem item : fullList) {
                if (item.getTableName().toLowerCase().contains(text)) filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

}