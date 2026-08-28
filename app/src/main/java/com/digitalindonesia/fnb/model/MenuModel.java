package com.digitalindonesia.fnb.model;

public class MenuModel {
    private int iconRes;
    private String title;
    private String subtitle;

    public MenuModel(int iconRes, String title) {
        this.iconRes = iconRes;
        this.title = title;
    }

    public int getIconRes() { return iconRes; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
}
