package com.emergency.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Unit {
    private final SimpleIntegerProperty unitId;
    private final SimpleStringProperty unitName;
    private final SimpleStringProperty unitType;

    public Unit(int id, String name, String type) {
        this.unitId = new SimpleIntegerProperty(id);
        this.unitName = new SimpleStringProperty(name);
        this.unitType = new SimpleStringProperty(type);
    }

    public int getUnitId() { return unitId.get(); }
    public String getUnitName() { return unitName.get(); }
    public String getUnitType() { return unitType.get(); }
}