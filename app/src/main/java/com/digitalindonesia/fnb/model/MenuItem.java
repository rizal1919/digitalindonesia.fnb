package com.digitalindonesia.fnb.model; // INI HARUS SESUAI PROJECTMU

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.digitalindonesia.fnb.model.MenuItem;
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

    // HAPUS tulisan com.example.foodorder.model, cukup tulis Ingredient saja
    private List<Ingredient> ingredients = new ArrayList<>();

    private int quantity;

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

    // HAPUS JUGA tulisan com.example.foodorder di getter setter ini
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }

    public double getTotalPriceForQuantity() {
        return price * Math.max(quantity, 0);
    }
}