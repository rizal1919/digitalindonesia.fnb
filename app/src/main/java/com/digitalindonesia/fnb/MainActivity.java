package com.digitalindonesia.fnb; // Pastikan ini sesuai dengan nama packagemu

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout menuContainer;
    private final List<View> menuRows = new ArrayList<>();

    // Data menu: label, icon, badge count (0 = tidak ada badge), badge type ("gray"/"red")
    private final String[] labels = {"Dashboard", "Analytics", "Messages", "Users", "Settings", "Notifications"};
    private final int[] icons = {
            R.drawable.ic_dashboard,
            R.drawable.ic_analytics,
            R.drawable.ic_messages,
            R.drawable.ic_user,
            R.drawable.ic_settings,
            R.drawable.ic_notification
    };
    private final int[] badgeCount = {0, 0, 6, 0, 0, 23};
    private final String[] badgeType = {"", "", "gray", "", "", "red"};

    private int selectedIndex = 1; // default: Analytics aktif kayak di desain

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bikin status bar transparan supaya gradient dashboard nyambung ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // <- Tambahkan baris ini
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

//        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//        MainActivity.this.startActivity(myIntent);
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
                selectItem(index);
                // TODO: pindah ke fragment/activity sesuai menu
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

    // Supaya tombol back menutup drawer dulu kalau lagi kebuka
    @Override
    public void onBackPressed() {
        View sidebar = findViewById(R.id.sidebarContainer);
        if (drawerLayout.isDrawerOpen(sidebar)) {
            drawerLayout.closeDrawer(sidebar);
        } else {
            super.onBackPressed();
        }
    }

    // Tambahkan fungsi ini agar bisa dipanggil oleh DashboardFragment
    public void openDrawer() {
        if (drawerLayout != null) {
            // Membuka navigasi laci dari kiri (START)
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}