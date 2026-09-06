package com.digitalindonesia.fnb.model;

import com.digitalindonesia.fnb.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HoldManager {
    private static HoldManager instance;
    private final List<HoldOrder> holdOrders = new ArrayList<>();

    private HoldManager() {}

    public static HoldManager getInstance() {
        if (instance == null) instance = new HoldManager();
        return instance;
    }

    public void addHoldOrder(HoldOrder order) {
        holdOrders.add(0, order); // Masukkan ke urutan paling atas
    }

    public List<HoldOrder> getHoldOrders() {
        return holdOrders;
    }

    public void removeHoldOrder(HoldOrder order) {
        holdOrders.remove(order);
    }

    // --- Model Internal ---
    public static class HoldOrder {
        public String id, trxNumber, date, customerName;
        public int queueNumber, totalItems;
        public double totalPrice;
        public List<MenuItem> items; // <-- BARU: Variabel penyimpan detail produk

        public HoldOrder(String trxNumber, int queueNumber, String date, String customerName, int totalItems, double totalPrice, List<MenuItem> items) {
            this.id = String.valueOf(System.currentTimeMillis());
            this.trxNumber = trxNumber;
            this.queueNumber = queueNumber;
            this.date = date;
            this.customerName = customerName;
            this.totalItems = totalItems;
            this.totalPrice = totalPrice;
            this.items = items;
        }
    }
}