package com.example.team1project;

public class DataSingleton {
    private static final DataSingleton instance = new DataSingleton();

    private String item;
    //private HashMap<String, String> dataMap = new HashMap<String, String>();

    private DataSingleton(){
    }

    public static DataSingleton getInstance(){
        return instance;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
