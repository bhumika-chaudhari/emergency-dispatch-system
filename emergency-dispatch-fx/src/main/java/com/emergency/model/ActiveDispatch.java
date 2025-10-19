package com.emergency.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActiveDispatch {
    private final SimpleIntegerProperty incidentId;
    private final SimpleStringProperty incidentType;
    private final SimpleStringProperty locationText;
    private final SimpleStringProperty priority;
    private final SimpleStringProperty assignedUnit;

    public ActiveDispatch(int id, String type, String location, String priority, String unitName) {
        this.incidentId = new SimpleIntegerProperty(id);
        this.incidentType = new SimpleStringProperty(type);
        this.locationText = new SimpleStringProperty(location);
        this.priority = new SimpleStringProperty(priority);
        this.assignedUnit = new SimpleStringProperty(unitName);
    }

    // --- CORRECT GETTERS AND PROPERTY ACCESSORS ---
    public int getIncidentId() { return incidentId.get(); }
    public SimpleIntegerProperty incidentIdProperty() { return incidentId; }

    public String getIncidentType() { return incidentType.get(); }
    public SimpleStringProperty incidentTypeProperty() { return incidentType; }

    public String getLocationText() { return locationText.get(); }
    public SimpleStringProperty locationTextProperty() { return locationText; }

    public String getPriority() { return priority.get(); }
    public SimpleStringProperty priorityProperty() { return priority; }

    public String getAssignedUnit() { return assignedUnit.get(); }
    public SimpleStringProperty assignedUnitProperty() { return assignedUnit; }
}