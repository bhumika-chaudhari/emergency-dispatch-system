package com.emergency.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class LocationHistory {
    private String note;
    private Timestamp createdAt;

    public LocationHistory(String note, Timestamp createdAt) {
        this.note = note;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        // This formats how each entry will look in the list
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdAt);
        return "[" + formattedDate + "] " + note;
    }
}