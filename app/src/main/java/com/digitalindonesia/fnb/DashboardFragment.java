package com.digitalindonesia.fnb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private boolean isSalesVisible = true;
    private String realSales = "Rp16.567.000";
    private String realProfit = "Rp4.200.000";
    private TextView tvTotalSales, tvTotalProfit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvTotalProfit = view.findViewById(R.id.tvTotalProfit);
        ImageView btnEye = view.findViewById(R.id.btnToggleEye);
        View btnDetail = view.findViewById(R.id.btnDetail);
        View btnExport = view.findViewById(R.id.btnExport);
        View btnLayani = view.findViewById(R.id.btnLayani);

        View menuProduk = view.findViewById(R.id.menuProduk);
        View menuRiwayat = view.findViewById(R.id.menuRiwayat);
        View menuStok = view.findViewById(R.id.menuStok);
        View menuLaporan = view.findViewById(R.id.menuLaporan);

        // toggle sembunyikan angka penjualan
//        btnEye.setOnClickListener(v -> {
//            isSalesVisible = !isSalesVisible;
//            tvTotalSales.setText(isSalesVisible ? realSales : "Rp••••••••");
//            btnEye.setImageResource(isSalesVisible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
//            // kalau belum ada ic_eye_off, buat 1 lagi vector "visibility_off"
//        });

        // Toggle sembunyikan angka penjualan & profit
        btnEye.setOnClickListener(v -> {
            isSalesVisible = !isSalesVisible;

            // Set text berdasarkan status visibility
            tvTotalSales.setText(isSalesVisible ? realSales : "Rp••••••••");
            tvTotalProfit.setText(isSalesVisible ? realProfit : "Rp••••••••");

            // Ubah icon
            btnEye.setImageResource(isSalesVisible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        });

        btnDetail.setOnClickListener(v -> Toast.makeText(getContext(), "Buka detail laporan", Toast.LENGTH_SHORT).show());
        btnExport.setOnClickListener(v -> Toast.makeText(getContext(), "Export laporan", Toast.LENGTH_SHORT).show());

        menuProduk.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Produk", Toast.LENGTH_SHORT).show());
        menuRiwayat.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Riwayat", Toast.LENGTH_SHORT).show());
        menuStok.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Stok", Toast.LENGTH_SHORT).show());
        menuLaporan.setOnClickListener(v -> Toast.makeText(getContext(), "Ke halaman Laporan", Toast.LENGTH_SHORT).show());

        // Floating button "Layani" -> nanti ganti ke Activity/Fragment ProsesPilihMenu
        btnLayani.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Buka menu Proses Pilih Menu", Toast.LENGTH_SHORT).show();
            // Contoh kalau pakai Fragment:
            // requireActivity().getSupportFragmentManager().beginTransaction()
            //     .replace(R.id.contentFrame, new ProsesPilihMenuFragment())
            //     .addToBackStack(null)
            //     .commit();
        });

        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        return view;
    }
}

