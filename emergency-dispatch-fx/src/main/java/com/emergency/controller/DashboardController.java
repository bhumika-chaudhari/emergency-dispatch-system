package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.ActiveDispatch;
import com.emergency.model.Unit; // <-- ADD THIS IMPORT
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // <-- ADD THIS IMPORT
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    // --- Incident Table Components ---
    @FXML private TableView<ActiveDispatch> activeIncidentsTable;
    @FXML private TableColumn<ActiveDispatch, Integer> incidentIdColumn;
    @FXML private TableColumn<ActiveDispatch, String> typeColumn;
    @FXML private TableColumn<ActiveDispatch, String> locationColumn;
    
    // --- NEW: Unit Table Components ---
    @FXML private TableView<Unit> availableUnitsTable;
    @FXML private TableColumn<Unit, String> unitNameColumn;
    @FXML private TableColumn<Unit, String> unitTypeColumn;

    // --- Button Components ---
    @FXML private Button newIncidentButton;
    @FXML private Button dispatchButton; // <-- NEW

    private IncidentDAO incidentDAO = new IncidentDAO();

    @FXML
    public void initialize() {
        // Setup for Incidents Table
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));

        // NEW: Setup for Units Table
        unitNameColumn.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        
        // Load initial data for both tables
        loadDispatchData();
        loadAvailableUnits();
        
        // Update Polling to refresh both tables
        Timeline poller = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> {
                loadDispatchData();
                loadAvailableUnits();
            })
        );
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }
    
    // --- NEW METHOD for the Dispatch button ---
    @FXML
    private void handleDispatchClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null || selectedUnit == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an incident AND an available unit to dispatch.");
            alert.show();
            return;
        }

        // Call the backend to perform the dispatch action
        incidentDAO.dispatchUnitToIncident(selectedIncident.getIncidentId(), selectedUnit.getUnitId());
        
        System.out.println("Dispatched unit " + selectedUnit.getUnitName() + " to incident " + selectedIncident.getIncidentId());
        
        // The real-time poller will automatically update the UI
    }

    @FXML
    private void handleNewIncidentClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/NewIncidentForm.fxml"));
            Parent root = loader.load();
            Stage formStage = new Stage();
            formStage.setTitle("Create New Incident");

            formStage.initModality(Modality.APPLICATION_MODAL); 
            formStage.setScene(new Scene(root));
            formStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDispatchData() {
        List<ActiveDispatch> dispatchesFromDB = incidentDAO.getActiveDispatches();
        activeIncidentsTable.setItems(FXCollections.observableArrayList(dispatchesFromDB));
        System.out.println("Incidents dashboard refreshed.");
    }

    // --- NEW METHOD to load units ---
    private void loadAvailableUnits() {
        List<Unit> unitsFromDB = incidentDAO.getAvailableUnits();
        availableUnitsTable.setItems(FXCollections.observableArrayList(unitsFromDB));
    }
}