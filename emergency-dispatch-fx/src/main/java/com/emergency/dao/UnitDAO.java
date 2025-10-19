package com.emergency.dao;

import com.emergency.model.Unit;
import com.emergency.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    public void createNewUnit(String unitName, String unitType) {
        String sql = "{CALL CreateNewUnit(?, ?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, unitName);
            cstmt.setString(2, unitType);
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUnitStatus(int unitId, String newStatus) {
        String sql = "{CALL UpdateUnitStatus(?, ?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, unitId);
            cstmt.setString(2, newStatus);
            cstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT unit_id, unit_name, type, status FROM Emergency_Units";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                units.add(new Unit(
                    rs.getInt("unit_id"),
                    rs.getString("unit_name"),
                    rs.getString("type"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
    }
    // In: com/emergency/dao/UnitDAO.java
// RENAME the method and add a WHERE clause to the SQL
public List<Unit> getAvailableUnits() {
    List<Unit> units = new ArrayList<>();
    String sql = "SELECT unit_id, unit_name, type, status FROM Emergency_Units WHERE status = 'Available'";
     try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                units.add(new Unit(
                    rs.getInt("unit_id"),
                    rs.getString("unit_name"),
                    rs.getString("type"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return units;
}
}