package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.ActiveDispatch;
import javafx.animation.KeyFrame; // <-- ADD THIS IMPORT
import javafx.animation.Timeline; // <-- ADD THIS IMPORT
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration; // <-- ADD THIS IMPORT
import java.util.List;

public class DashboardController {

    @FXML private TableView<ActiveDispatch> activeIncidentsTable;
    @FXML private TableColumn<ActiveDispatch, Integer> incidentIdColumn;
    @FXML private TableColumn<ActiveDispatch, String> typeColumn;
    @FXML private TableColumn<ActiveDispatch, String> locationColumn;
    
    private IncidentDAO incidentDAO = new IncidentDAO();

    @FXML
    public void initialize() {
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));

        loadDispatchData();

        // --- NEW CODE FOR REAL-TIME REFRESH ---
        Timeline poller = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> loadDispatchData())
        );
        poller.setCycleCount(Timeline.INDEFINITE); // Run forever
        poller.play();
        // --- END OF NEW CODE ---
    }
    
    private void loadDispatchData() {
        List<ActiveDispatch> dispatchesFromDB = incidentDAO.getActiveDispatches();
        ObservableList<ActiveDispatch> data = FXCollections.observableArrayList(dispatchesFromDB);
        activeIncidentsTable.setItems(data);
        System.out.println("Dashboard refreshed."); // Added for testing
    }
}