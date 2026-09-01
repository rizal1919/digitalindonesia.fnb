package com.digitalindonesia.fnb.model;

public class CartLineItem {
    private String name;
    private String variant;
    private double price;
    private int quantity;
    private int imageResId;

    public CartLineItem(String name, String variant, double price, int quantity, int imageResId) {
        this.name = name;
        this.variant = variant;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getVariant() { return variant; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getImageResId() { return imageResId; }
}
