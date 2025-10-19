package com.emergency.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActiveDispatch {
    private final SimpleIntegerProperty incidentId;
    private final SimpleStringProperty incidentType;
    private final SimpleStringProperty locationText;

    public ActiveDispatch(int id, String type, String location) {
        this.incidentId = new SimpleIntegerProperty(id);
        this.incidentType = new SimpleStringProperty(type);
        this.locationText = new SimpleStringProperty(location);
    }

    // Getters required by JavaFX TableView
    public int getIncidentId() { return incidentId.get(); }
    public String getIncidentType() { return incidentType.get(); }
    public String getLocationText() { return locationText.get(); }
}