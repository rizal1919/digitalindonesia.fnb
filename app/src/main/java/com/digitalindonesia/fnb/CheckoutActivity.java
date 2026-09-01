package com.digitalindonesia.fnb;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.digitalindonesia.fnb.fragments.CheckoutCartFragment;
import com.digitalindonesia.fnb.fragments.CheckoutPaymentFragment;
import com.digitalindonesia.fnb.fragments.CheckoutReceiptFragment;

/**
 * Activity yang menampung alur checkout 3 langkah:
 * 1. Keranjang -> 2. Pembayaran -> 3. Tanda Terima
 *
 * Navigasi antar step dikendalikan lewat tombol di masing-masing fragment
 * (bukan swipe manual), makanya ViewPager2 di-set userInputEnabled(false).
 */
public class CheckoutActivity extends AppCompatActivity implements CheckoutStepNavigator {

    private ViewPager2 viewPagerCheckout;
    private TextView tvCheckoutTitle;

    // Tracker views
    private TextView circleStep1, circleStep2, circleStep3;
    private TextView labelStep1, labelStep2, labelStep3;
    private android.view.View lineStep1, lineStep2;


    // Variabel Container Sidebar
    private android.widget.LinearLayout menuContainer;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayoutCheckout;
    private final java.util.List<android.view.View> menuRows = new java.util.ArrayList<>();

    // Data Menu Khusus Checkout
    private final String[] labels = {
            "Kembali ke Beranda",
            "Tunda Transaksi",
            "Batalkan Pesanan"
    };

    // Ikon yang perlu kamu siapkan di folder drawable
    private final int[] icons = {
            R.drawable.ic_dashboard, // Ikon home
            R.drawable.ic_riwayat,   // Ikon pause/tunda (bisa diganti sesuai kebutuhan)
            R.drawable.ic_delete     // Ikon tong sampah atau silang
    };

    // Hilangkan badge untuk menu checkout
    private final int[] badgeCount = {0, 0, 0};
    private final String[] badgeType = {"", "", ""};

    private int currentStep = 0; // index 0,1,2 (step 1,2,3)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        bindViews();

        viewPagerCheckout.setAdapter(new CheckoutPagerAdapter(this));
        viewPagerCheckout.setUserInputEnabled(false); // Navigasi hanya lewat tombol, bukan swipe

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> handleBackNavigation());

        updateProgressTracker(0);


        // Inisialisasi Sidebar
        drawerLayoutCheckout = findViewById(R.id.drawerLayoutCheckout);
        menuContainer = findViewById(R.id.menuContainer); // ID ini ada di dalam sidebar_content.xml

        // Hubungkan tombol upgrade premium (yang ada di dalam sidebar_content.xml)
        android.widget.TextView btnUpgradePremium = findViewById(R.id.btnUpgradePremium);
        if (btnUpgradePremium != null) {
            btnUpgradePremium.setOnClickListener(v -> {
                android.widget.Toast.makeText(CheckoutActivity.this, "Fitur ini dinonaktifkan saat Checkout", android.widget.Toast.LENGTH_SHORT).show();
                drawerLayoutCheckout.closeDrawer(androidx.core.view.GravityCompat.START);
            });
        }

        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayoutCheckout != null) {
                drawerLayoutCheckout.openDrawer(androidx.core.view.GravityCompat.START);
            }
        });

        // Panggil fungsi perakit menu
        buildMenuItems();
    }

    private void buildMenuItems() {
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        // Pastikan menuContainer kosong sebelum diisi ulang
        if(menuContainer != null) {
            menuContainer.removeAllViews();
        }

        for (int i = 0; i < labels.length; i++) {
            android.view.View row = inflater.inflate(R.layout.item_sidebar_menu, menuContainer, false);

            ImageView icon = row.findViewById(R.id.icon);
            TextView label = row.findViewById(R.id.label);
            TextView badge = row.findViewById(R.id.badge);

            icon.setImageResource(icons[i]);
            label.setText(labels[i]);
            badge.setVisibility(android.view.View.GONE); // Hilangkan badge

            final int index = i;
            row.setOnClickListener(v -> {
                drawerLayoutCheckout.closeDrawer(findViewById(R.id.sidebarContainer));

                if (labels[index].equals("Batalkan Pesanan")) {
                    android.widget.Toast.makeText(this, "Pesanan Dibatalkan", android.widget.Toast.LENGTH_SHORT).show();
                    // Kosongkan keranjang
                    com.digitalindonesia.fnb.cart.CartManager.getInstance().clear();
                    finish(); // Kembali ke halaman sebelumnya
                } else if(labels[index].equals("Tunda Transaksi")) {
                    android.widget.Toast.makeText(this, "Transaksi Ditunda", android.widget.Toast.LENGTH_SHORT).show();
                    // Logika simpan keranjang sementara ke SQLite
                    finish();
                } else {
                    // Kembali ke Beranda
                    android.content.Intent intent = new android.content.Intent(CheckoutActivity.this, MainActivity.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            if (menuContainer != null) {
                menuContainer.addView(row);
            }
            menuRows.add(row);
        }
    }

    private void bindViews() {
        viewPagerCheckout = findViewById(R.id.viewPagerCheckout);
        tvCheckoutTitle = findViewById(R.id.tvCheckoutTitle);

        circleStep1 = findViewById(R.id.circleStep1);
        circleStep2 = findViewById(R.id.circleStep2);
        circleStep3 = findViewById(R.id.circleStep3);

        labelStep1 = findViewById(R.id.labelStep1);
        labelStep2 = findViewById(R.id.labelStep2);
        labelStep3 = findViewById(R.id.labelStep3);

        lineStep1 = findViewById(R.id.lineStep1);
        lineStep2 = findViewById(R.id.lineStep2);
    }

    private void handleBackNavigation() {
        if (currentStep > 0) {
            goToStep(currentStep - 1);
        } else {
            finish();
        }
    }

    @Override
    public void goToStep(int stepIndex) {
        currentStep = stepIndex;
        viewPagerCheckout.setCurrentItem(stepIndex, true);
        tvCheckoutTitle.setText("Checkout (" + (stepIndex + 1) + "/3)");
        updateProgressTracker(stepIndex);
    }

    @Override
    public void goToNextStep() {
        if (currentStep < 2) {
            goToStep(currentStep + 1);
        }
    }

    /**
     * Update tampilan tracker: step sebelum currentStep -> completed,
     * step == currentStep -> active, step setelahnya -> inactive.
     */
    private void updateProgressTracker(int activeStepIndex) {
        // Tambahkan argumen angka "1", "2", "3" di parameter paling belakang
        setStepState(circleStep1, labelStep1, getStepState(0, activeStepIndex), "1");
        setStepState(circleStep2, labelStep2, getStepState(1, activeStepIndex), "2");
        setStepState(circleStep3, labelStep3, getStepState(2, activeStepIndex), "3");

        // Garis di antara step ikut oranye kalau step sebelumnya sudah dilewati/aktif
        lineStep1.setBackgroundResource(activeStepIndex >= 1 ? R.drawable.line_dashed_active : R.drawable.line_dashed);
        lineStep2.setBackgroundResource(activeStepIndex >= 2 ? R.drawable.line_dashed_active : R.drawable.line_dashed);
    }



    private int getStepState(int stepIndex, int activeStepIndex) {
        // 0 = completed, 1 = active, 2 = inactive
        if (stepIndex < activeStepIndex) return 0;
        if (stepIndex == activeStepIndex) return 1;
        return 2;
    }

    // Tambahkan parameter String stepNumber
    private void setStepState(TextView circle, TextView label, int state, String stepNumber) {
        switch (state) {
            case 0: // completed
                circle.setBackgroundResource(R.drawable.step_circle_completed);
                circle.setText("✓");
                circle.setTextColor(getResources().getColor(android.R.color.white));
                label.setTextColor(getResources().getColor(android.R.color.black));
                break;
            case 1: // active
                circle.setBackgroundResource(R.drawable.step_circle_active);
                circle.setText(stepNumber); // Kembalikan ke angka aslinya
                circle.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                label.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                break;
            default: // inactive
                circle.setBackgroundResource(R.drawable.step_circle_inactive);
                circle.setText(stepNumber); // Kembalikan ke angka aslinya
                circle.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                label.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }

    /**
     * Adapter ViewPager2 untuk 3 fragment step checkout.
     */
    private static class CheckoutPagerAdapter extends FragmentStateAdapter {

        CheckoutPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new CheckoutPaymentFragment();
                case 2:
                    return new CheckoutReceiptFragment();
                default:
                    return new CheckoutCartFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
