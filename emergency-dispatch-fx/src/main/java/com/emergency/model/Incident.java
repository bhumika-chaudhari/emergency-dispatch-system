package com.emergency.model;

// This is a simple data class, no JavaFX properties needed
public class Incident {
    private int id;
    private String type;
    private String description;
    private String locationText;
    private String priority;
    private int severityLevel;
    // You can add more fields here like callerName, callerPhone etc.

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public int getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(int severityLevel) { this.severityLevel = severityLevel; }
}