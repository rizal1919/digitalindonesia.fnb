package com.digitalindonesia.fnb;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.digitalindonesia.fnb.model.Ingredient;
import com.digitalindonesia.fnb.model.MenuItem;
import com.digitalindonesia.fnb.model.I;
import com.digitalindonesia.fnb.adapter.MenuAdapter;
import com.digitalindonesia.fnb.cart.CartManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuListFragment extends Fragment implements MenuAdapter.OnMenuItemActionListener {

    private RecyclerView rvMenu;
    private EditText etSearch;
    private TextView tvClear;

    private MenuAdapter adapter;
    private final List<MenuItem> fullMenuList = new ArrayList<>();
    private final List<MenuItem> filteredMenuList = new ArrayList<>();

    private final CartManager.CartListener cartListener = (totalQty, totalPrice) -> {
        // Notifikasi ke Activity supaya FAB ikut ter-refresh.
        if (getActivity() instanceof CartUpdateHost) {
            ((CartUpdateHost) getActivity()).onCartSummaryChanged(totalQty, totalPrice);
        }
    };

    /** Implement interface ini di MainActivity supaya FAB bisa menampilkan ringkasan cart. */
    public interface CartUpdateHost {
        void onCartSummaryChanged(int totalQuantity, double totalPrice);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMenu = view.findViewById(R.id.rvMenu);
        etSearch = view.findViewById(R.id.etSearch);
        tvClear = view.findViewById(R.id.tvClear);

        setupDummyData();

        adapter = new MenuAdapter(filteredMenuList, this);
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMenu.setAdapter(adapter);

        setupSearch();
        tvClear.setOnClickListener(v -> {
            etSearch.setText("");
            filterMenu("");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CartManager.getInstance().addListener(cartListener);
        // Trigger sekali supaya FAB langsung sinkron begitu halaman ini terlihat lagi
        // (misalnya setelah kembali dari Detail Menu).
        CartManager cart = CartManager.getInstance();
        if (getActivity() instanceof CartUpdateHost) {
            ((CartUpdateHost) getActivity())
                    .onCartSummaryChanged(cart.getTotalQuantity(), cart.getTotalPrice());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        CartManager.getInstance().removeListener(cartListener);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterMenu(String query) {
        filteredMenuList.clear();
        String lowerQuery = query.toLowerCase(Locale.getDefault()).trim();
        for (MenuItem item : fullMenuList) {
            if (lowerQuery.isEmpty() || item.getName().toLowerCase(Locale.getDefault()).contains(lowerQuery)) {
                filteredMenuList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCardClicked(MenuItem item, int position) {
        // Hapus casting com.example.foodorder.model-nya
        MenuDetailBottomSheet.newInstance(item)
                .show(getParentFragmentManager(), MenuDetailBottomSheet.TAG);
    }

    @Override
    public void onAddClicked(MenuItem item, int position) {
        CartManager.getInstance().addOne(item);
        adapter.refreshItem(position);
    }

    /** Ganti dengan data asli dari API/database. */
    private void setupDummyData() {
        MenuItem nasiGoreng = new MenuItem(1, "Nasi Goreng",
                "Nasi goreng khas rumahan dengan telur dan ayam suwir.", 11.99, R.drawable.ic_add);
        nasiGoreng.setIngredients(dummyIngredients());

        MenuItem saladBuah = new MenuItem(2, "Salad Buah",
                "Campuran buah segar dengan saus yogurt.", 22.99, R.drawable.ic_add);
        saladBuah.setIngredients(dummyIngredients());

        MenuItem telurDadar = new MenuItem(3, "Telur Dadar Mandalika",
                "Veg cheese grilled sandwich / vegetable cheese Sandwich Vegetarian.", 14.99, R.drawable.ic_add);
        telurDadar.setIngredients(dummyIngredients());
        telurDadar.setFavoriteCount(45);

        fullMenuList.add(nasiGoreng);
        fullMenuList.add(saladBuah);
        fullMenuList.add(telurDadar);

        filteredMenuList.addAll(fullMenuList);
    }

    private List<Ingredient> dummyIngredients() {
        List<Ingredient> list = new ArrayList<>();
        list.add(new Ingredient("Bawang putih", R.drawable.ic_add));
        list.add(new Ingredient("Cabai", R.drawable.ic_add));
        list.add(new Ingredient("Labu", R.drawable.ic_add));
        list.add(new Ingredient("Selada", R.drawable.ic_add));
        list.add(new Ingredient("Tomat", R.drawable.ic_add));
        return list;
    }
}