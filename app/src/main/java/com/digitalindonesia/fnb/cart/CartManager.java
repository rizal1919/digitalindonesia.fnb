package com.digitalindonesia.fnb.cart;

import android.view.MenuItem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CartManager {

    public interface CartListener {
        void onCartChanged(int totalQuantity, double totalPrice);
    }

    private static volatile CartManager instance;

    // key = id menu, value = MenuItem (quantity disimpan di dalam objeknya)
    private final Map<Integer, MenuItem> cartItems = new LinkedHashMap<>();
    private final CopyOnWriteArrayList<CartListener> listeners = new CopyOnWriteArrayList<>();

    private CartManager() { }

    public static CartManager getInstance() {
        if (instance == null) {
            synchronized (CartManager.class) {
                if (instance == null) {
                    instance = new CartManager();
                }
            }
        }
        return instance;
    }

    public void addListener(CartListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CartListener listener) {
        listeners.remove(listener);
    }

    /** Tambah 1 qty untuk item ini (dipanggil saat tombol '+' di List Menu diklik). */
    public synchronized void addOne(MenuItem item) {
        setQuantity(item, getQuantity(item.getId()) + 1);
    }

    /** Kurangi 1 qty untuk item ini. */
    public synchronized void removeOne(MenuItem item) {
        setQuantity(item, getQuantity(item.getId()) - 1);
    }

    /** Set qty absolut untuk item (dipakai dari Detail Menu). Qty 0 -> item dihapus dari cart. */
    public synchronized void setQuantity(MenuItem item, int newQuantity) {
        int qty = Math.max(0, newQuantity);
        item.setQuantity(qty);
        if (qty == 0) {
            cartItems.remove(item.getId());
        } else {
            cartItems.put(item.getId(), item);
        }
        notifyListeners();
    }

    public synchronized int getQuantity(int itemId) {
        MenuItem existing = cartItems.get(itemId);
        return existing == null ? 0 : existing.getQuantity();
    }

    public synchronized int getTotalQuantity() {
        int total = 0;
        for (MenuItem item : cartItems.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    public synchronized double getTotalPrice() {
        double total = 0;
        for (MenuItem item : cartItems.values()) {
            total += item.getTotalPriceForQuantity();
        }
        return total;
    }

    public synchronized void clear() {
        cartItems.clear();
        notifyListeners();
    }

    private void notifyListeners() {
        int totalQty = getTotalQuantity();
        double totalPrice = getTotalPrice();
        for (CartListener l : listeners) {
            l.onCartChanged(totalQty, totalPrice);
        }
    }
}
