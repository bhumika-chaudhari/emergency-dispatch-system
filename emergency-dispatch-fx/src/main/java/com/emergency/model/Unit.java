package com.emergency.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Unit {
    private final SimpleIntegerProperty unitId;
    private final SimpleStringProperty unitName;
    private final SimpleStringProperty unitType;
    private final SimpleStringProperty status;

    public Unit(int id, String name, String type, String status) {
        this.unitId = new SimpleIntegerProperty(id);
        this.unitName = new SimpleStringProperty(name);
        this.unitType = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
    }

    // --- CORRECT GETTERS AND PROPERTY ACCESSORS ---
    public int getUnitId() { return unitId.get(); }
    public SimpleIntegerProperty unitIdProperty() { return unitId; }

    public String getUnitName() { return unitName.get(); }
    public SimpleStringProperty unitNameProperty() { return unitName; }

    public String getUnitType() { return unitType.get(); }
    public SimpleStringProperty unitTypeProperty() { return unitType; }
    
    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
}