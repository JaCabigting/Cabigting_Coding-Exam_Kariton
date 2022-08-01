package com.example.kariton.Models;

import java.net.URL;

public class CartItem {

    private String name;
    private String description;
    private String salePrice;
    private String originalPrice;
    private String percentOff;
    private String size;
    private String bitmap;
    private int quantity;

    // Constructor
    public CartItem(String name, String description, String salePrice, String originalPrice, String percentOff, String size, String bitmap, int quantity) {
        this.name = name;
        this.description = description;
        this.salePrice = salePrice;
        this.originalPrice = originalPrice;
        this.percentOff = percentOff;
        this.size = size;
        this.bitmap = bitmap;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(String percentOff) {
        this.percentOff = percentOff;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
