package com.emergency.model;

public class Location {
    private int id;
    private String name;

    public Location(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // This method is called by the ComboBox to display the location name
    @Override
    public String toString() {
        return name;
    }
}