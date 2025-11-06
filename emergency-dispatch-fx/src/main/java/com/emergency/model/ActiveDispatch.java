package com.emergency.model;

public class ActiveDispatch {
    private int incidentId;
    private String incidentType;
    private String locationText;
    private String priority;
    private String assignedUnit; 
    private String unitType;      
    private String status;        
    private String dispatchStatus;

    public ActiveDispatch(int incidentId, String incidentType, String locationText, String priority,
                          String assignedUnit, String unitType, String status, String dispatchStatus) {
        this.incidentId = incidentId;
        this.incidentType = incidentType;
        this.locationText = locationText;
        this.priority = priority;
        this.assignedUnit = assignedUnit;
        this.unitType = unitType;
        this.status = status;
        this.dispatchStatus = dispatchStatus;
    }

    // Getters
    public int getIncidentId() { return incidentId; }
    public String getIncidentType() { return incidentType; }
    public String getLocationText() { return locationText; }
    public String getPriority() { return priority; }
    public String getAssignedUnit() { return assignedUnit; }
    public String getUnitType() { return unitType; }
    public String getStatus() { return status; }  
    public String getDispatchStatus() { return dispatchStatus; }
}
