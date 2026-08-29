package com.digitalindonesia.fnb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.adapter.MenuAdapter;
import com.digitalindonesia.fnb.cart.CartManager;
import com.digitalindonesia.fnb.model.MenuModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private boolean isSalesVisible = true;
    private String realSales = "Rp16.567.000";
    private String realProfit = "Rp4.200.000";
    private TextView tvTotalSales, tvTotalProfit;

    // Variabel untuk tombol floating
    private View btnLayani;
    private TextView tvBtnLayani;

    private RecyclerView rvMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvTotalProfit = view.findViewById(R.id.tvTotalProfit);
        ImageView btnEye = view.findViewById(R.id.btnToggleEye);
        View btnDetail = view.findViewById(R.id.btnDetail);
        View btnExport = view.findViewById(R.id.btnExport);

        // Hubungkan variabel dengan layout
        btnLayani = view.findViewById(R.id.btnLayani);
        tvBtnLayani = view.findViewById(R.id.tvBtnLayani);

        RelativeLayout menuProduk = view.findViewById(R.id.menuProduk);
        RelativeLayout menuRiwayat = view.findViewById(R.id.menuRiwayat);
        RelativeLayout menuStok = view.findViewById(R.id.menuStok);
        RelativeLayout menuLaporan = view.findViewById(R.id.menuLaporan);

        btnEye.setOnClickListener(v -> {
            isSalesVisible = !isSalesVisible;
            tvTotalSales.setText(isSalesVisible ? realSales : "Rp••••••••");
            tvTotalProfit.setText(isSalesVisible ? realProfit : "Rp••••••••");
            btnEye.setImageResource(isSalesVisible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        });

        btnDetail.setOnClickListener(v -> Toast.makeText(getContext(), "Buka detail laporan", Toast.LENGTH_SHORT).show());
        btnExport.setOnClickListener(v -> Toast.makeText(getContext(), "Export laporan", Toast.LENGTH_SHORT).show());
        menuProduk.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Produk", Toast.LENGTH_SHORT).show());
        menuRiwayat.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Riwayat", Toast.LENGTH_SHORT).show());
        menuStok.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Stok", Toast.LENGTH_SHORT).show());
        menuLaporan.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Laporan", Toast.LENGTH_SHORT).show());

        // Aksi klik untuk Floating Button
        btnLayani.setOnClickListener(v -> openMenuListPage());

        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        // 1. Inisialisasi RecyclerView
        rvMenu = view.findViewById(R.id.rvMenuTransaksi);

        // 2. Siapkan Datanya
        List<MenuModel> listMenu = new ArrayList<>();
        listMenu.add(new MenuModel(R.drawable.ic_produk, "Reservasi"));
        listMenu.add(new MenuModel(R.drawable.ic_riwayat, "Supplier"));
        listMenu.add(new MenuModel(R.drawable.ic_stok, "Bahan Baku"));
        listMenu.add(new MenuModel(R.drawable.ic_analytics, "Harga Grosir"));
        listMenu.add(new MenuModel(R.drawable.ic_karyawan, "Karyawan"));
        listMenu.add(new MenuModel(R.drawable.ic_kasir, "Pengeluaran"));
        listMenu.add(new MenuModel(R.drawable.ic_customers, "Pelanggan"));
        listMenu.add(new MenuModel(R.drawable.ic_metode_bayar, "Metode Bayar"));
        listMenu.add(new MenuModel(R.drawable.ic_resi, "Cetak Resi"));

        // 3. Atur Layout Menjadi Horizontal
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 4. Pasang Adapter & Atur Klik-nya
        MenuAdapter adapter = new MenuAdapter(listMenu, menu -> {
            Toast.makeText(getContext(), "Ke halaman: " + menu.getTitle(), Toast.LENGTH_SHORT).show();
        });

        rvMenu.setAdapter(adapter);

        return view;
    }

    private void openMenuListPage() {
        // PERBAIKAN: Gunakan requireActivity().getSupportFragmentManager() untuk mengganti fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, new MenuListFragment()) // Pastikan id container di MainActivity kamu benar
                .addToBackStack(null) // Agar user bisa kembali dengan tombol back
                .commit();

        refreshCartFab(CartManager.getInstance().getTotalQuantity(),
                CartManager.getInstance().getTotalPrice());
    }

    public void onCartSummaryChanged(int totalQuantity, double totalPrice) {
        refreshCartFab(totalQuantity, totalPrice);
    }

    private void refreshCartFab(int totalQuantity, double totalPrice) {
        if (totalQuantity <= 0) {
            tvBtnLayani.setText("Mulai Transaksi");
            return;
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        String priceText = format.format(totalPrice).replace("Rp", "Rp ");

        // PERBAIKAN: Set text ke TextView yang ada di dalam tombol floating
        tvBtnLayani.setText(totalQuantity + " item • " + priceText);
    }
}