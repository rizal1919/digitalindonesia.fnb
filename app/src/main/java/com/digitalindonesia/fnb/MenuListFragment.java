package com.digitalindonesia.fnb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalindonesia.fnb.model.Ingredient;
import com.digitalindonesia.fnb.model.MenuItem;
import com.digitalindonesia.fnb.adapter.ProductAdapter;
import com.digitalindonesia.fnb.cart.CartManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;

public class MenuListFragment extends Fragment implements ProductAdapter.OnMenuItemActionListener {

    private RecyclerView rvMenu;
    private View floatingCartContainer;
    private TextView tvCartQty, tvCartPrice, tvPromoMessage;
    private EditText etSearch;
    private TextView tvClear;
    private ChipGroup chipGroupCategory;

    // ==== BARU: Header & Tab Order Type (pengganti Spinner + EditText nama pelanggan) ====
    private ImageView btnBack;
    private TextView tabDineIn, tabPickUp, tabDelivery;
    private String currentOrderType = "Dine In"; // Default tab aktif

    private ImageView btnToggleView;
    private boolean isGridView = true; // Default ke Grid
    private SharedPreferences prefs;

    private ProductAdapter adapter;
    private final List<MenuItem> fullMenuList = new ArrayList<>();
    private final List<MenuItem> filteredMenuList = new ArrayList<>();

    // Simpan status filter saat ini
    private String currentSearchQuery = "";
    private String currentCategory = "Semua"; // Default category

    private final CartManager.CartListener cartListener = (totalQty, totalPrice) -> {
        if (getActivity() instanceof CartUpdateHost) {
            ((CartUpdateHost) getActivity()).onCartSummaryChanged(totalQty, totalPrice);
        }
        updateFloatingCart(totalQty, totalPrice);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    };

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

        prefs = requireActivity().getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        isGridView = prefs.getBoolean("PREF_IS_GRID", true); // Baca cache, default true

        rvMenu = view.findViewById(R.id.rvMenu);
        btnToggleView = view.findViewById(R.id.btnToggleView);

        etSearch = view.findViewById(R.id.etSearch);
        tvClear = view.findViewById(R.id.tvClear);
        chipGroupCategory = view.findViewById(R.id.chipGroupCategory);

        setupDummyData();
        setupOrderTypeTabs(view);
        setupCategories();

        adapter = new ProductAdapter(filteredMenuList, this);
        rvMenu.setAdapter(adapter); // Set adapter dulu

        applyViewMode();

        btnToggleView.setOnClickListener(v -> {
            isGridView = !isGridView; // Balikkan status
            prefs.edit().putBoolean("PREF_IS_GRID", isGridView).apply(); // Simpan ke cache
            applyViewMode(); // Render ulang layout
        });

        setupSearch();

        tvClear.setOnClickListener(v -> {
            etSearch.setText("");
            currentSearchQuery = "";
            applyFilters();
        });

        // Tombol Back di header baru
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        floatingCartContainer = view.findViewById(R.id.floatingCartContainer);
        tvCartQty = view.findViewById(R.id.tvCartQty);
        tvCartPrice = view.findViewById(R.id.tvCartPrice);
        tvPromoMessage = view.findViewById(R.id.tvPromoMessage);
        View btnCheckout = view.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(v -> {
            // Validasi pencegahan: Jangan biarkan user masuk ke halaman checkout jika keranjang kosong
            if (CartManager.getInstance().getTotalQuantity() <= 0) {
                Toast.makeText(getContext(), "Keranjang masih kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buat jalur perpindahan ke CheckoutActivity
            Intent intent = new Intent(requireActivity(), CheckoutActivity.class);

            // (Opsional) Jika kamu ingin membawa data Tipe Pesanan dari Tab Segmen (Dine In/Pick Up/Delivery)
            // intent.putExtra("EXTRA_ORDER_TYPE", namaVariabelTipePesananKamu);

            startActivity(intent);
        });
    }

    // ==== BARU: Setup Tab Order Type (pengganti setupOrderTypeSpinner) ====
    private void setupOrderTypeTabs(View root) {
        tabDineIn = root.findViewById(R.id.tabDineIn);
        tabPickUp = root.findViewById(R.id.tabPickUp);
        tabDelivery = root.findViewById(R.id.tabDelivery);

        tabDineIn.setOnClickListener(v -> selectOrderTypeTab(tabDineIn, "Dine In"));
        tabPickUp.setOnClickListener(v -> selectOrderTypeTab(tabPickUp, "Pick Up"));
        tabDelivery.setOnClickListener(v -> selectOrderTypeTab(tabDelivery, "Delivery"));

        // Set default state saat pertama kali dibuka
        selectOrderTypeTab(tabDineIn, "Dine In");
    }

    private void selectOrderTypeTab(TextView selectedTab, String orderType) {
        currentOrderType = orderType;

        TextView[] allTabs = {tabDineIn, tabPickUp, tabDelivery};
        for (TextView tab : allTabs) {
            boolean isSelected = (tab == selectedTab);
            tab.setBackgroundResource(isSelected ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
            tab.setTextColor(isSelected
                    ? getResources().getColor(android.R.color.white)
                    : getResources().getColor(android.R.color.white));
            // Catatan: kalau mau teks tab non-aktif warna gelap/abu-abu (karena background
            // transparan di atas foto), ganti baris di atas sesuai kebutuhan desain, misal:
            // tab.setTextColor(isSelected ? Color.WHITE : Color.parseColor("#EFEFEF"));
        }
    }

    private void applyViewMode() {
        // 1. Beritahu Adapter status terbaru
        if (adapter != null) {
            adapter.setGridView(isGridView);
        }

        // 2. Ubah LayoutManager dan Icon
        if (isGridView) {
            rvMenu.setLayoutManager(new GridLayoutManager(getContext(), 2));
            btnToggleView.setImageResource(R.drawable.ic_menu_grid); // Icon list (hint: klik untuk jadi list)
        } else {
            rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
            btnToggleView.setImageResource(R.drawable.ic_menu_list); // Icon grid (hint: klik untuk jadi grid)
        }

        // 3. Render ulang list dengan XML yang baru
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupCategories() {
        // Ini bisa didapat dari SQLite nanti
        String[] categories = {"Semua", "Makanan", "Minuman", "Snack", "Dessert"};

        for (String category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);

            // Pilih "Semua" secara default
            if (category.equals("Semua")) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentCategory = category;
                    applyFilters();
                }
            });

            chipGroupCategory.addView(chip);
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase(Locale.getDefault()).trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // Fungsi tunggal untuk filter Search & Kategori secara bersamaan
    private void applyFilters() {
        filteredMenuList.clear();

        for (MenuItem item : fullMenuList) {
            // Asumsi: Kamu sudah menambahkan property "category" dan method getCategory() di MenuItem.java
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    item.getName().toLowerCase(Locale.getDefault()).contains(currentSearchQuery);

            // Kalau kamu belum pasang getCategory() di MenuItem, hapus baris getCategory ini smentara biar ga error
            // boolean matchesCategory = currentCategory.equals("Semua") ||
            //                          item.getCategory().equalsIgnoreCase(currentCategory);

            // Hapus baris ini kalau getCategory() sudah diimplementasikan
            boolean matchesCategory = currentCategory.equals("Semua") || true;

            if (matchesSearch && matchesCategory) {
                filteredMenuList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        CartManager cart = CartManager.getInstance();
        cart.addListener(cartListener);
        if (getActivity() instanceof CartUpdateHost) {
            ((CartUpdateHost) getActivity()).onCartSummaryChanged(cart.getTotalQuantity(), cart.getTotalPrice());
        }
        updateFloatingCart(cart.getTotalQuantity(), cart.getTotalPrice());
        adapter.notifyDataSetChanged();
    }

    private void updateFloatingCart(int totalQty, double totalPrice) {
        if (totalQty > 0) {
            floatingCartContainer.setVisibility(View.VISIBLE);
            tvCartQty.setText(totalQty + " produk");
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            format.setMaximumFractionDigits(0);
            tvCartPrice.setText(format.format(totalPrice).replace("Rp", "Rp"));

            if (totalQty < 4) {
                int butuh = 4 - totalQty;
                tvPromoMessage.setText("Tambah " + butuh + " produk lagi dapatkan disc 30%");
            } else {
                tvPromoMessage.setText("Klaim diskon 30% di halaman pembayaran");
            }
        } else {
            floatingCartContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CartManager.getInstance().removeListener(cartListener);
    }

    @Override
    public void onCardClicked(MenuItem item, int position) {
        MenuDetailBottomSheet.newInstance(item)
                .show(getParentFragmentManager(), MenuDetailBottomSheet.TAG);
    }

    @Override
    public void onIncreaseClicked(MenuItem item, int position) {
        // Cek stok sebelum menambah
        if (item.getQuantity() >= item.getStock()) {
            Toast.makeText(getContext(), "Stok tidak mencukupi!", Toast.LENGTH_SHORT).show();
            return;
        }

        CartManager.getInstance().addOne(item);
        adapter.refreshItem(position);
    }

    @Override
    public void onDecreaseClicked(MenuItem item, int position) {
        CartManager.getInstance().removeOne(item);
        adapter.refreshItem(position); // Akan otomatis mengubah tampilan ke tombol + jika qty jadi 0
    }

    private void setupDummyData() {
        // Parameter terakhir adalah angka stok
        MenuItem nasiGoreng = new MenuItem(1, "Nasi Goreng",
                "Nasi goreng khas rumahan dengan telur dan ayam suwir.", 11.99, R.drawable.img_nasgor, 15);
        nasiGoreng.setIngredients(dummyIngredients());

        MenuItem saladBuah = new MenuItem(2, "Salad Buah",
                "Campuran buah segar dengan saus yogurt.", 22.99, R.drawable.img_salad, 0); // Contoh stok habis
        saladBuah.setIngredients(dummyIngredients());

        MenuItem telurDadar = new MenuItem(3, "Telur Dadar Mandalika",
                "Veg cheese grilled sandwich / vegetable cheese Sandwich Vegetarian.", 14.99, R.drawable.img_dadar, 8);
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
