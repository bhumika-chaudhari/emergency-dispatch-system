package com.emergency.dao;

import com.emergency.model.ActiveDispatch;
import com.emergency.model.Incident;
import com.emergency.model.Unit;
import com.emergency.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {




    
    public List<ActiveDispatch> getActiveDispatches() {
    List<ActiveDispatch> dispatches = new ArrayList<>();
    // Updated SQL query to include priority
    String sql = "SELECT incident_id, incident_type, location_text, priority FROM vw_ActiveDispatches";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            // Updated constructor call to include priority
            dispatches.add(new ActiveDispatch(
                rs.getInt("incident_id"),
                rs.getString("incident_type"),
                rs.getString("location_text"),
                rs.getString("priority") 
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return dispatches;
}
    // Add this new method inside your IncidentDAO class
public Incident getIncidentDetailsById(int incidentId) {
    String sql = "SELECT * FROM Incidents WHERE incident_id = ?";
    Incident incident = null;

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            incident = new Incident();
            incident.setId(rs.getInt("incident_id"));
            incident.setType(rs.getString("type"));
            incident.setDescription(rs.getString("description"));
            incident.setLocationText(rs.getString("location_text"));
            incident.setPriority(rs.getString("priority"));
            incident.setSeverityLevel(rs.getInt("severity_level"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return incident;
}
public int createNewIncident(String firstName, String lastName, String phone, String type, String description, String locationText) {
    String sql = "{CALL CreateNewIncident(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    int newIncidentId = -1; // Default value if something goes wrong

    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        // Set all the IN (input) parameters for the procedure
        cstmt.setString(1, firstName);
        cstmt.setString(2, lastName);
        cstmt.setString(3, phone);
        cstmt.setString(4, type);
        cstmt.setString(5, description);
        cstmt.setString(6, locationText);
        cstmt.setBigDecimal(7, null); // Latitude - for now we'll pass null
        cstmt.setBigDecimal(8, null); // Longitude - for now we'll pass null
        cstmt.setString(9, "High");   // Priority - default to High
        cstmt.setInt(10, 3);          // Severity - default to 3
        cstmt.setInt(11, 101);        // created_by_user_id - placeholder

        // Register the OUT (output) parameter to get the new ID back
        cstmt.registerOutParameter(12, java.sql.Types.INTEGER);

        // Execute the procedure
        cstmt.execute();

        // Get the new incident ID from the OUT parameter
        newIncidentId = cstmt.getInt(12);

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return newIncidentId;
}
// Add this method to get all available units
public List<Unit> getAvailableUnits() {
    List<Unit> units = new ArrayList<>();
    String sql = "SELECT unit_id, unit_name, type FROM Emergency_Units WHERE status = 'Available'";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            units.add(new Unit(
                rs.getInt("unit_id"),
                rs.getString("unit_name"),
                rs.getString("type")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return units;
}

// Add this method to call your stored procedure
public void dispatchUnitToIncident(int incidentId, int unitId) {
    String sql = "{CALL DispatchUnitToIncident(?, ?, ?)}";

    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setInt(1, unitId);
        cstmt.setInt(2, incidentId);
        cstmt.setString(3, "Dispatched from JavaFX application");
        cstmt.execute();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
