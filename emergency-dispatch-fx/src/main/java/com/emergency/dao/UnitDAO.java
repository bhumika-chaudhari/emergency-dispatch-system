package com.emergency.dao;

import com.emergency.model.Location;
import com.emergency.model.Unit;
import com.emergency.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    public boolean createNewUnit(String unitName, String unitType, int locationId) {
        String sql = "{CALL CreateNewUnit(?, ?, ?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, unitName);
            cstmt.setString(2, unitType);
            cstmt.setInt(3, locationId);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

  public void updateUnitStatus(int unitId, String newStatus) {
    String sql = "{CALL UpdateUnitStatus(?, ?)}";
    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        // Update the emergency unit
        cstmt.setInt(1, unitId);
        cstmt.setString(2, newStatus);
        cstmt.execute();

        // Now sync the dispatch
        String dispatchSql = null;

        if (newStatus.equalsIgnoreCase("On Scene")) {
            dispatchSql = "UPDATE dispatches SET status='On Scene', arrival_time=NOW() WHERE unit_id=? AND status='Enroute'";
        } else if (newStatus.equalsIgnoreCase("Available")) {
            dispatchSql = "UPDATE dispatches SET status='Cleared', clear_time=NOW() WHERE unit_id=? AND status IN ('Enroute','On Scene')";
        }

        if (dispatchSql != null) {
            try (PreparedStatement ps = conn.prepareStatement(dispatchSql)) {
                ps.setInt(1, unitId);
                ps.executeUpdate();
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT location_id, name FROM Locations ORDER BY name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                locations.add(new Location(
                    rs.getInt("location_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    // Method to get ALL units, regardless of status
    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT eu.unit_id, eu.unit_name, eu.type, eu.status, loc.name AS location_name " +
                     "FROM Emergency_Units eu " +
                     "LEFT JOIN Locations loc ON eu.current_location_id = loc.location_id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                units.add(new Unit(
                    rs.getInt("unit_id"),
                    rs.getString("unit_name"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("location_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }

    // Method to get ONLY available units
    public List<Unit> getAvailableUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT eu.unit_id, eu.unit_name, eu.type, eu.status, loc.name AS location_name " +
                     "FROM Emergency_Units eu " +
                     "LEFT JOIN Locations loc ON eu.current_location_id = loc.location_id " +
                     "WHERE eu.status = 'Available'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                units.add(new Unit(
                    rs.getInt("unit_id"),
                    rs.getString("unit_name"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("location_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }
}