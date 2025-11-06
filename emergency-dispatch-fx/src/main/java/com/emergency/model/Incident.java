package com.emergency.model;

public class Incident {
    private int id;
    private String type;
    private String description;
    private String locationText;
    private String priority;
    private int severityLevel;
    private String status;

    // --- Getters ---
    public int getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getLocationText() { return locationText; }
    public String getPriority() { return priority; }
    public int getSeverityLevel() { return severityLevel; }
    public String getStatus() { return status; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setLocationText(String locationText) { this.locationText = locationText; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setSeverityLevel(int severityLevel) { this.severityLevel = severityLevel; }
    public void setStatus(String status) { this.status = status; } // ✅ public
}
