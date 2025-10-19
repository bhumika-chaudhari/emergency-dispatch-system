package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.ActiveDispatch;
import com.emergency.model.Incident; // <-- ADD THIS IMPORT
import com.emergency.model.Unit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea; // <-- ADD THIS IMPORT
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.util.List;

public class DashboardController {

    // --- Incident Table Components ---
    @FXML private TableView<ActiveDispatch> activeIncidentsTable;
    @FXML private TableColumn<ActiveDispatch, Integer> incidentIdColumn;
    @FXML private TableColumn<ActiveDispatch, String> typeColumn;
    @FXML private TableColumn<ActiveDispatch, String> locationColumn;
    @FXML private TableColumn<ActiveDispatch, String> priorityColumn;
    @FXML private Button closeIncidentButton;
    // --- Unit Table Components ---
    @FXML private TableView<Unit> availableUnitsTable;
    @FXML private TableColumn<Unit, String> unitNameColumn;
    @FXML private TableColumn<Unit, String> unitTypeColumn;

    // --- NEW: DETAIL VIEW COMPONENT ---
    @FXML private TextArea incidentDetailsArea;

    // --- Button Components ---
    @FXML private Button newIncidentButton;
    @FXML private Button dispatchButton;

    private IncidentDAO incidentDAO = new IncidentDAO();

    @FXML
    public void initialize() {
        // Setup for Incidents Table
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        // Setup for Units Table
        unitNameColumn.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<>("unitType"));

        // Code to style high-priority rows
        activeIncidentsTable.setRowFactory(tv -> new TableRow<ActiveDispatch>() {
            @Override
            protected void updateItem(ActiveDispatch item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("high-priority-row");
                if (item != null && !empty && "High".equalsIgnoreCase(item.getPriority())) {
                    getStyleClass().add("high-priority-row");
                }
            }
        });
        
        // --- NEW: LISTEN FOR TABLE ROW SELECTION ---
        activeIncidentsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayIncidentDetails(newSelection);
                }
            }
        );

        // Load initial data
        loadDispatchData();
        loadAvailableUnits();
        
        // Setup polling
        Timeline poller = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            loadDispatchData();
            loadAvailableUnits();
        }));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
        
        // Add icon to button
        FontAwesomeIconView plusIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE);
        plusIcon.setFill(javafx.scene.paint.Color.WHITE);
        newIncidentButton.setGraphic(plusIcon);
    }
    @FXML
private void handleCloseIncidentClick() {
    // Get the currently selected incident from the main table
    ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();

    if (selectedIncident == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an incident to close.");
        alert.show();
        return;
    }

    // Call the backend DAO method to close the incident
    incidentDAO.closeIncident(selectedIncident.getIncidentId());

    System.out.println("Closed incident with ID: " + selectedIncident.getIncidentId());

    // The real-time poller will automatically refresh the table,
    // and the closed incident will disappear from the view.
}
    // --- NEW METHOD TO DISPLAY DETAILS ---
    private void displayIncidentDetails(ActiveDispatch summary) {
        Incident fullDetails = incidentDAO.getIncidentDetailsById(summary.getIncidentId());

        if (fullDetails != null) {
            StringBuilder details = new StringBuilder();
            details.append("INCIDENT DETAILS\n");
            details.append("----------------\n");
            details.append("ID: ").append(fullDetails.getId()).append("\n");
            details.append("Type: ").append(fullDetails.getType()).append("\n");
            details.append("Priority: ").append(fullDetails.getPriority()).append("\n");
            details.append("Severity: ").append(fullDetails.getSeverityLevel()).append("\n");
            details.append("Location: ").append(fullDetails.getLocationText()).append("\n\n");
            details.append("Description:\n").append(fullDetails.getDescription());

            incidentDetailsArea.setText(details.toString());
        } else {
            incidentDetailsArea.clear();
        }
    }
    @FXML
    private void handleDispatchClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null || selectedUnit == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an incident AND an available unit to dispatch.");
            alert.show();
            return;
        }

        incidentDAO.dispatchUnitToIncident(selectedIncident.getIncidentId(), selectedUnit.getUnitId());
        System.out.println("Dispatched unit " + selectedUnit.getUnitName() + " to incident " + selectedIncident.getIncidentId());
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
    
    private void loadAvailableUnits() {
        List<Unit> unitsFromDB = incidentDAO.getAvailableUnits();
        availableUnitsTable.setItems(FXCollections.observableArrayList(unitsFromDB));
    }
}