package com.digitalindonesia.fnb;

/**
 * Dipakai oleh fragment-fragment step (Cart, Payment, Receipt) untuk
 * memberitahu Activity agar berpindah step, tanpa fragment perlu tahu
 * detail ViewPager2 atau progress tracker.
 */
public interface CheckoutStepNavigator {
    void goToStep(int stepIndex); // 0 = Keranjang, 1 = Pembayaran, 2 = Tanda Terima
    void goToNextStep();
}