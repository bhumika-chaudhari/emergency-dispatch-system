package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.IncidentStatusLog;
import com.emergency.model.LocationHistory;
import com.emergency.model.UnitStatusLog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class LogsViewController {

    // --- FXML UI Components ---
    @FXML private ListView<IncidentStatusLog> incidentStatusListView;
    @FXML private ListView<UnitStatusLog> unitStatusListView;
    @FXML private ListView<LocationHistory> locationHistoryListView;

    // --- DAO Instance ---
    private IncidentDAO incidentDAO = new IncidentDAO();

    /**
     * This method is called by the DashboardController to pass in the
     * incident ID and load all the log data.
     */
    public void loadLogs(int incidentId) {
        // 1. Load Incident Status Logs
        List<IncidentStatusLog> incidentLogs = incidentDAO.getIncidentStatusLogs(incidentId);
        incidentStatusListView.setItems(FXCollections.observableArrayList(incidentLogs));

        // 2. Load Unit Status Logs
        List<UnitStatusLog> unitLogs = incidentDAO.getUnitStatusLogs(incidentId);
        unitStatusListView.setItems(FXCollections.observableArrayList(unitLogs));

        // 3. Load Location History Logs
        List<LocationHistory> locationLogs = incidentDAO.getLocationHistory(incidentId);
        locationHistoryListView.setItems(FXCollections.observableArrayList(locationLogs));
    }
}