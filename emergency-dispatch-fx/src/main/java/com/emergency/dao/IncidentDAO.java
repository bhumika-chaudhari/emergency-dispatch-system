package com.emergency.dao;

import com.emergency.model.ActiveDispatch;
import com.emergency.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {
    public List<ActiveDispatch> getActiveDispatches() {
        List<ActiveDispatch> dispatches = new ArrayList<>();
        String sql = "SELECT incident_id, incident_type, location_text FROM vw_ActiveDispatches";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                dispatches.add(new ActiveDispatch(
                    rs.getInt("incident_id"),
                    rs.getString("incident_type"),
                    rs.getString("location_text")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dispatches;
    }
}
