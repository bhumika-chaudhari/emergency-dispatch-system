package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.ActiveDispatch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- ADD THIS IMPORT
import javafx.scene.Parent;   // <-- ADD THIS IMPORT
import javafx.scene.Scene;     // <-- ADD THIS IMPORT
import javafx.scene.control.Button; // <-- ADD THIS IMPORT
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality; // <-- ADD THIS IMPORT
import javafx.stage.Stage;     // <-- ADD THIS IMPORT
import javafx.util.Duration;

import java.io.IOException; // <-- ADD THIS IMPORT
import java.util.List;

public class DashboardController {

    @FXML private TableView<ActiveDispatch> activeIncidentsTable;
    @FXML private TableColumn<ActiveDispatch, Integer> incidentIdColumn;
    @FXML private TableColumn<ActiveDispatch, String> typeColumn;
    @FXML private TableColumn<ActiveDispatch, String> locationColumn;
    
    @FXML private Button newIncidentButton; // <-- ADD THIS VARIABLE

    private IncidentDAO incidentDAO = new IncidentDAO();

    @FXML
    public void initialize() {
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));

        loadDispatchData();
        
        Timeline poller = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> loadDispatchData())
        );
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    // --- NEW METHOD FOR THE BUTTON CLICK ---
    @FXML
    private void handleNewIncidentClick() {
        try {
            // Load the FXML file for the new form window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/NewIncidentForm.fxml"));
            Parent root = loader.load();

            // Create a new window (Stage) for the form
            Stage formStage = new Stage();
            formStage.setTitle("Create New Incident");
            // This blocks the main window while the form is open
            formStage.initModality(Modality.APPLICATION_MODAL); 
            formStage.setScene(new Scene(root));

            // Show the form and wait for it to be closed
            formStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDispatchData() {
        // ... (your existing data loading code)
        List<ActiveDispatch> dispatchesFromDB = incidentDAO.getActiveDispatches();
        ObservableList<ActiveDispatch> data = FXCollections.observableArrayList(dispatchesFromDB);
        activeIncidentsTable.setItems(data);
        System.out.println("Dashboard refreshed.");
    }
}