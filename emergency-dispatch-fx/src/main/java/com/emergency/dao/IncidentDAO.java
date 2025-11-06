package com.emergency.dao;

import com.emergency.model.ActiveDispatch;
import com.emergency.model.Caller;
import com.emergency.model.Incident;
import com.emergency.model.IncidentStatusLog;
import com.emergency.model.LocationHistory;
import com.emergency.model.UnitStatusLog;
import com.emergency.model.Witness;
import com.emergency.util.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

   
  public List<ActiveDispatch> getActiveDispatches() {
    List<ActiveDispatch> dispatches = new ArrayList<>();
    String sql = "SELECT incident_id, incident_type, location_text, priority, " +
                 "unit_name, unit_type, incident_status, dispatch_status " +
                 "FROM vw_activedispatches";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            dispatches.add(new ActiveDispatch(
                rs.getInt("incident_id"),
                rs.getString("incident_type"),
                rs.getString("location_text"),
                rs.getString("priority"),
                rs.getString("unit_name"),
                rs.getString("unit_type"),
                rs.getString("incident_status"),
                rs.getString("dispatch_status")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return dispatches;
}


// In IncidentDAO.java
public boolean addWitness(int incidentId, String firstName, String lastName, String phone, String statement) {
    String sql = "{CALL AddWitness(?, ?, ?, ?, ?)}";
    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setInt(1, incidentId);
        cstmt.setString(2, firstName);
        cstmt.setString(3, lastName);
        cstmt.setString(4, phone);
        cstmt.setString(5, statement);
        cstmt.execute();
        
        // If execute() completes without error, return true
        return true; 

    } catch (SQLException e) {
        // If an error occurs, print it and return false
        e.printStackTrace();
        return false; 
    }
}
    public void closeIncident(int incidentId) {
    String sql = "UPDATE Incidents SET status='Closed' WHERE incident_id=?";
    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        // 1️⃣ Close the incident
        ps.setInt(1, incidentId);
        ps.executeUpdate();

        // 2️⃣ Mark dispatches as Cleared
        String clearDispatchesSql = """
            UPDATE dispatches
            SET status = 'Cleared',
                clear_time = NOW()
            WHERE incident_id = ?
              AND status IN ('Enroute', 'On Scene')
        """;
        try (PreparedStatement ps2 = conn.prepareStatement(clearDispatchesSql)) {
            ps2.setInt(1, incidentId);
            ps2.executeUpdate();
        }

        // 3️⃣ Set units back to Available
        String updateUnitsSql = """
            UPDATE Emergency_Units
            SET status = 'Available'
            WHERE unit_id IN (
                SELECT unit_id
                FROM dispatches
                WHERE incident_id = ?
            )
        """;
        try (PreparedStatement ps3 = conn.prepareStatement(updateUnitsSql)) {
            ps3.setInt(1, incidentId);
            ps3.executeUpdate();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Add these two new methods inside your IncidentDAO class

public List<LocationHistory> getLocationHistory(int incidentId) {
    List<LocationHistory> history = new ArrayList<>();
    String sql = "SELECT note, created_at FROM Incident_Location_History WHERE incident_id = ? ORDER BY created_at DESC";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            history.add(new LocationHistory(
                rs.getString("note"),
                rs.getTimestamp("created_at")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return history;
}

public void addLocationHistory(int incidentId, String note) {
    String sql = "{CALL AddLocationHistoryNote(?, ?)}";

    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setInt(1, incidentId);
        cstmt.setString(2, note);
        cstmt.execute();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    // Replace the old createNewIncident method with this one
public int createNewIncident(String firstName, String lastName, String phone, String type, String description, String locationText, String priority, int severity) {
    String sql = "{CALL CreateNewIncident(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    int newIncidentId = -1;

    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {

        cstmt.setString(1, firstName);
        cstmt.setString(2, lastName);
        cstmt.setString(3, phone);
        cstmt.setString(4, type);
        cstmt.setString(5, description);
        cstmt.setString(6, locationText);
        cstmt.setBigDecimal(7, null); // Latitude placeholder
        cstmt.setBigDecimal(8, null); // Longitude placeholder
        
        // Use the values from the form instead of hardcoded values
        cstmt.setString(9, priority);
        cstmt.setInt(10, severity);
        
        cstmt.setInt(11, 101); // created_by_user_id placeholder
        cstmt.registerOutParameter(12, java.sql.Types.INTEGER);
        cstmt.execute();
        newIncidentId = cstmt.getInt(12);

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return newIncidentId;
}
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
    public Caller getCallerDetails(int incidentId) {
    String sql = "SELECT c.* FROM Callers c JOIN Incidents i ON c.caller_id = i.caller_id WHERE i.incident_id = ?";
    Caller caller = null;

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            caller = new Caller(
                rs.getInt("caller_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone_number")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return caller;
}

// Update the getWitnesses method
public List<Witness> getWitnesses(int incidentId) {
    List<Witness> witnesses = new ArrayList<>();
    // Include witness_id in the query
    String sql = "SELECT witness_id, first_name, last_name, phone_number, statement FROM Witnesses WHERE incident_id = ?";

    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            witnesses.add(new Witness(
                rs.getInt("witness_id"), // <-- Pass the ID
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getString("phone_number"),
                rs.getString("statement")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return witnesses;
}
// Add this method inside IncidentDAO.java
public List<IncidentStatusLog> getIncidentStatusLogs(int incidentId) {
    List<IncidentStatusLog> logs = new ArrayList<>();
    String sql = "SELECT old_status, new_status, timestamp " +
                 "FROM Incident_Status_Log WHERE incident_id = ? ORDER BY timestamp DESC";
    
    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            // Updated constructor call to remove 'comment'
            logs.add(new IncidentStatusLog(
                rs.getString("old_status"),
                rs.getString("new_status"),
                rs.getTimestamp("timestamp")
            ));
        }
    } catch (SQLException e) { 
        e.printStackTrace(); 
    }
    return logs;
}
// Add this new method
public void deleteWitness(int witnessId) {
    String sql = "{CALL DeleteWitness(?)}";
    try (Connection conn = DatabaseConnector.getConnection();
         CallableStatement cstmt = conn.prepareCall(sql)) {
        cstmt.setInt(1, witnessId);
        cstmt.execute();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
// In IncidentDAO.java
public List<UnitStatusLog> getUnitStatusLogs(int incidentId) {
    List<UnitStatusLog> logs = new ArrayList<>();
    
    // UPDATED SQL: Removed 'usl.comment' from the SELECT list
    String sql = "SELECT u.unit_name, usl.old_status, usl.new_status, usl.timestamp " +
                 "FROM Unit_Status_Log usl " +
                 "JOIN Emergency_Units u ON usl.unit_id = u.unit_id " +
                 "JOIN Dispatches d ON u.unit_id = d.unit_id " +
                 "WHERE d.incident_id = ? " +
                 "ORDER BY usl.timestamp DESC";
    
    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, incidentId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            // The constructor call is now correct (4 arguments)
            logs.add(new UnitStatusLog(
                rs.getString("unit_name"),
                rs.getString("old_status"),
                rs.getString("new_status"),
                rs.getTimestamp("timestamp")
            ));
        }
    } catch (SQLException e) { 
        e.printStackTrace(); 
    }
    return logs;
}

public Incident getIncidentDetailsById(int incidentId) { String sql = "SELECT * FROM Incidents WHERE incident_id = ?"; Incident incident = null; try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) { pstmt.setInt(1, incidentId); ResultSet rs = pstmt.executeQuery(); if (rs.next()) { incident = new Incident(); incident.setId(rs.getInt("incident_id")); incident.setType(rs.getString("type")); incident.setDescription(rs.getString("description")); incident.setLocationText(rs.getString("location_text")); incident.setStatus(rs.getString("status")); incident.setPriority(rs.getString("priority")); incident.setSeverityLevel(rs.getInt("severity_level")); } } catch (SQLException e) { e.printStackTrace(); } return incident; }
}