// In: com/emergency/model/ActiveDispatch.java
package com.emergency.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ActiveDispatch {
    private final SimpleIntegerProperty incidentId;
    private final SimpleStringProperty incidentType;
    private final SimpleStringProperty locationText;
    private final SimpleStringProperty priority; // <-- ADD THIS

    public ActiveDispatch(int id, String type, String location, String priority) { // <-- ADD PRIORITY
        this.incidentId = new SimpleIntegerProperty(id);
        this.incidentType = new SimpleStringProperty(type);
        this.locationText = new SimpleStringProperty(location);
        this.priority = new SimpleStringProperty(priority); // <-- ADD THIS
    }

    // Getters
    public int getIncidentId() { return incidentId.get(); }
    public String getIncidentType() { return incidentType.get(); }
    public String getLocationText() { return locationText.get(); }
    public String getPriority() { return priority.get(); } // <-- ADD THIS
}