package com.example.team1project;

public class DataSingleton {
    private static final DataSingleton instance = new DataSingleton();

    private String item;
    private String price;
    private String region;

    private DataSingleton() {
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public static DataSingleton getInstance() {
        return instance;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}