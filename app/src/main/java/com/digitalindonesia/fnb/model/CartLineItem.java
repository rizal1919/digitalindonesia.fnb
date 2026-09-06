package com.digitalindonesia.fnb.model;

public class CartLineItem {
    private MenuItem menuItem; // Simpan objek aslinya untuk referensi ke CartManager
    private String variant;

    public CartLineItem(MenuItem menuItem, String variant) {
        this.menuItem = menuItem;
        this.variant = variant;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public String getVariant() { return variant; }

    // Ambil data langsung dari MenuItem agar selalu sinkron
    public String getName() { return menuItem.getName(); }
    public double getPrice() { return menuItem.getPrice(); }
    public int getQuantity() { return menuItem.getQuantity(); }
    public int getImageResId() { return menuItem.getImageResId(); }
}