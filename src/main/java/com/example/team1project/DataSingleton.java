package com.example.team1project;

import java.util.ArrayList;

public class DataSingleton {
    private static final DataSingleton instance = new DataSingleton();

    private String item;
    private String selectedRegion;
    private ArrayList<String> selectedRegions = new ArrayList<>();//다중 지역 선택
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

    public ArrayList<String> getSelectedRegions() {
        return selectedRegions;
    }

    public void setSelectedRegions(ArrayList<String> selectedRegions) {
        this.selectedRegions = selectedRegions;
    }

    public String getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion) {
        this.selectedRegion = selectedRegion;
    }
}