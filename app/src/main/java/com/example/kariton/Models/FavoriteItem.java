package com.example.kariton.Models;

import com.google.gson.JsonParser;

import org.json.JSONObject;

public class FavoriteItem {

    private String name;
    private String description;
    private String salePrice;
    private String originalPrice;
    private String percentOff;
    private String bitmap;
    private JSONObject[] sizes = new JSONObject[5];
    private int index;

    // Constructor
    public FavoriteItem(String name, String description, String salePrice, String originalPrice, String percentOff, String bitmap) {
        this.name = name;
        this.description = description;
        this.salePrice = salePrice;
        this.originalPrice = originalPrice;
        this.percentOff = percentOff;
        this.bitmap = bitmap;
    }

    // Getters and Setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public JSONObject[] getSizes() {
        return sizes;
    }

    public void setSizes(JSONObject[] sizes) {
        this.sizes = sizes;
    }
}
