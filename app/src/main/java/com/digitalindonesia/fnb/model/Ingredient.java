package com.digitalindonesia.fnb.model;

import java.io.Serializable;

/** Model sederhana untuk satu bahan makanan di RecyclerView horizontal. */
public class Ingredient implements Serializable {

    private String name;
    private int imageResId;

    public Ingredient(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}
