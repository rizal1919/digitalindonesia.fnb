package com.digitalindonesia.fnb;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Tambahan untuk memunculkan pesan

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout menuContainer;
    private final List<View> menuRows = new ArrayList<>();

    // 1. TAMBAH "Logout" di posisi paling akhir
    private final String[] labels = {"Dashboard", "Analytics", "Messages", "Users", "Settings", "Notifications", "Logout"};
    private final int[] icons = {
            R.drawable.ic_dashboard,
            R.drawable.ic_analytics,
            R.drawable.ic_messages,
            R.drawable.ic_user,
            R.drawable.ic_settings,
            R.drawable.ic_notification,
            R.drawable.ic_logout // Pastikan kamu punya icon ic_logout di folder drawable
    };

    // Tambah angka 0 dan string "" untuk indeks si Logout
    private final int[] badgeCount = {0, 0, 6, 0, 0, 23, 0};
    private final String[] badgeType = {"", "", "gray", "", "", "red", ""};

    private int selectedIndex = 1; // default: Analytics aktif

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuContainer = findViewById(R.id.menuContainer);

        buildMenuItems();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, new DashboardFragment())
                    .commit();
        }

        // 2. FUNGSI KLIK UNTUK TOMBOL UPGRADE PREMIUM
        TextView btnUpgradePremium = findViewById(R.id.btnUpgradePremium);
        if (btnUpgradePremium != null) {
            btnUpgradePremium.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Buka Halaman Premium!", Toast.LENGTH_SHORT).show();
                // Tutup drawer setelah diklik
                drawerLayout.closeDrawer(findViewById(R.id.sidebarContainer));

                // TODO: Kode untuk pindah ke Activity/Fragment Premium
            });
        }
    }

    private void buildMenuItems() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < labels.length; i++) {
            View row = inflater.inflate(R.layout.item_sidebar_menu, menuContainer, false);

            ImageView icon = row.findViewById(R.id.icon);
            TextView label = row.findViewById(R.id.label);
            TextView badge = row.findViewById(R.id.badge);

            icon.setImageResource(icons[i]);
            label.setText(labels[i]);

            if (badgeCount[i] > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(badgeCount[i]));
                if (badgeType[i].equals("red")) {
                    badge.setBackgroundResource(R.drawable.bg_badge_red);
                    badge.setTextColor(getResources().getColor(R.color.badge_red_text));
                } else {
                    badge.setBackgroundResource(R.drawable.bg_badge_gray);
                    badge.setTextColor(getResources().getColor(R.color.text_gray));
                }
            } else {
                badge.setVisibility(View.GONE);
            }

            final int index = i;
            row.setOnClickListener(v -> {

                // 3. CEK APAKAH YANG DIKLIK ADALAH LOGOUT
                if (labels[index].equals("Logout")) {
                    Toast.makeText(MainActivity.this, "Proses Logout...", Toast.LENGTH_SHORT).show();
                    // TODO: Hapus session, pindah ke LoginActivity, lalu panggil finish();
                } else {
                    selectItem(index);
                    // TODO: Pindah ke fragment/activity sesuai menu biasa
                }

                // Tutup sidebar
                drawerLayout.closeDrawer(findViewById(R.id.sidebarContainer));
            });

            menuContainer.addView(row);
            menuRows.add(row);
        }

        selectItem(selectedIndex);
    }

    private void selectItem(int index) {
        selectedIndex = index;
        for (int i = 0; i < menuRows.size(); i++) {
            View row = menuRows.get(i);
            TextView label = row.findViewById(R.id.label);
            ImageView icon = row.findViewById(R.id.icon);

            if (i == index) {
                row.setBackgroundResource(R.drawable.bg_menu_selected);
                label.setTextColor(getResources().getColor(android.R.color.white));
                icon.setColorFilter(getResources().getColor(android.R.color.white));
            } else {
                row.setBackground(null);
                label.setTextColor(getResources().getColor(R.color.text_dark));
                icon.setColorFilter(getResources().getColor(R.color.text_dark));
            }
        }
    }

    @Override
    public void onBackPressed() {
        View sidebar = findViewById(R.id.sidebarContainer);
        if (drawerLayout.isDrawerOpen(sidebar)) {
            drawerLayout.closeDrawer(sidebar);
        } else {
            super.onBackPressed();
        }
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}