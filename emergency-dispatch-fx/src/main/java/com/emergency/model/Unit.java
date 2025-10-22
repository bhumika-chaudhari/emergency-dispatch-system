package com.emergency.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Unit {
    private final SimpleIntegerProperty unitId;
    private final SimpleStringProperty unitName;
    private final SimpleStringProperty unitType;
    private final SimpleStringProperty status;
    private final SimpleStringProperty locationName; // <-- ADD THIS

    public Unit(int id, String name, String type, String status, String locationName) { // <-- ADD locationName
        this.unitId = new SimpleIntegerProperty(id);
        this.unitName = new SimpleStringProperty(name);
        this.unitType = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
        this.locationName = new SimpleStringProperty(locationName); // <-- ADD THIS
    }

    // --- Getters and Property Methods ---
    public int getUnitId() { return unitId.get(); }
    public SimpleIntegerProperty unitIdProperty() { return unitId; }
    public String getUnitName() { return unitName.get(); }
    public SimpleStringProperty unitNameProperty() { return unitName; }
    public String getUnitType() { return unitType.get(); }
    public SimpleStringProperty unitTypeProperty() { return unitType; }
    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
    public String getLocationName() { return locationName.get(); } // <-- ADD THIS
    public SimpleStringProperty locationNameProperty() { return locationName; } // <-- ADD THIS
}