package com.emergency.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UnitStatusLog {
    private String unitName, oldStatus, newStatus; // 'comment' field removed
    private Timestamp timestamp;

    // 'comment' parameter removed from constructor
    public UnitStatusLog(String unitName, String oldStatus, String newStatus, Timestamp timestamp) {
        this.unitName = unitName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        // 'comment' removed from the format string
        return String.format("[%s] Unit '%s' changed from '%s' to '%s'.", 
                             time, unitName, oldStatus, newStatus);
    }
}