package com.digitalindonesia.fnb.model; // INI HARUS SESUAI PROJECTMU

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.digitalindonesia.fnb.model.Ingredient;

public class MenuItem implements Serializable {

    private int id;
    private String name;
    private String description;
    private double price;
    private int imageResId;
    private boolean favorite;
    private int favoriteCount;
    private int stock;
    private String note = "";
    private double customPrice = 0; // 0 berarti pakai harga asli
    private double discount = 0;
    private boolean isDiscountPercent = false;
    private int quantity;

    private List<Ingredient> ingredients = new ArrayList<>();

    public MenuItem(int id, String name, String description,
                    double price, int imageResId, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = 0;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = Math.max(0, stock); }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public double getCustomPrice() {
        return customPrice > 0 ? customPrice : getPrice();
    }
    public void setCustomPrice(double customPrice) { this.customPrice = customPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public boolean isDiscountPercent() { return isDiscountPercent; }
    public void setDiscountPercent(boolean percent) { this.isDiscountPercent = percent; }

    // RUMUS HARGA BARU
    public double getFinalPricePerUnit() {
        double base = getCustomPrice();
        double potong = isDiscountPercent ? (base * (discount / 100)) : discount;
        return base - potong;
    }

    // Fungsi ini disisakan SATU saja yang versi terbaru
    public double getTotalPriceForQuantity() {
        return getFinalPricePerUnit() * getQuantity();
    }
}