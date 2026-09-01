package com.digitalindonesia.fnb.model;

public class TableItem {
    private int id;
    private String tableName; // Contoh: "Meja 1", "VIP-A"
    private int capacity;
    private String area; // Contoh: "Indoor", "Outdoor", "Lantai 2"
    private boolean isAvailable; // Untuk menandai meja kosong/terisi

    public TableItem(String tableName, int capacity, String area) {
        this.tableName = tableName;
        this.capacity = capacity;
        this.area = area;
        this.isAvailable = true; // Default meja kosong
    }

    public TableItem(int id, String tableName, int capacity, String area, boolean isAvailable) {
        this.id = id;
        this.tableName = tableName;
        this.capacity = capacity;
        this.area = area;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }
    public String getTableName() { return tableName; }
    public int getCapacity() { return capacity; }
    public String getArea() { return area; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) { isAvailable = available; }
}