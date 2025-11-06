package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.dao.UnitDAO;
import com.emergency.model.ActiveDispatch;
import com.emergency.model.LocationHistory;
import com.emergency.model.Unit;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    @FXML private TableColumn<ActiveDispatch, Void> actionColumn;
    // --- FXML UI Components ---
    @FXML private TableView<ActiveDispatch> activeIncidentsTable;
    @FXML private TableColumn<ActiveDispatch, Integer> incidentIdColumn;
    @FXML private TableColumn<ActiveDispatch, String> typeColumn;
    @FXML private TableColumn<ActiveDispatch, String> locationColumn;
    @FXML private TableColumn<ActiveDispatch, String> priorityColumn;
    @FXML private TableColumn<ActiveDispatch, String> assignedUnitColumn;
    @FXML private TableColumn<ActiveDispatch, String> incidentStatusColumn;
    @FXML private TableView<Unit> availableUnitsTable;
    @FXML private Button viewLogsButton;
    @FXML private TableColumn<Unit, String> unitNameColumn;
    @FXML private TableColumn<Unit, String> unitTypeColumn;
    @FXML private TableColumn<Unit, String> unitStatusColumn;
    @FXML private TableColumn<Unit, String> unitLocationColumn;
    @FXML private ListView<LocationHistory> locationHistoryView;
    @FXML private TextField newLocationNoteField;
    @FXML private Button addLocationNoteButton;
    @FXML private Button newIncidentButton;
    @FXML private Button dispatchButton;
    @FXML private Button closeIncidentButton;
    @FXML private Button addUnitButton;
    @FXML private Button setOnSceneButton;
    @FXML private Button setClearButton;

    // --- DAO Instances ---
    private IncidentDAO incidentDAO = new IncidentDAO();
    private UnitDAO unitDAO = new UnitDAO();

    private ActiveDispatch currentlySelectedIncident;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupRowFactories();
        setupEventListeners(); 
        setupActionColumn();
        loadInitialData();
        setupPolling();
        setupButtonIcons();
    }

    private void setupTableColumns() {
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        assignedUnitColumn.setCellValueFactory(new PropertyValueFactory<>("assignedUnit"));
        incidentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        unitNameColumn.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        unitStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        unitLocationColumn.setCellValueFactory(new PropertyValueFactory<>("locationName"));
    }

    private void setupRowFactories() {
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
    }

    private void setupEventListeners() {
        activeIncidentsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    this.currentlySelectedIncident = newSelection;
                    displayLocationHistory(newSelection);
                } else {
                    this.currentlySelectedIncident = null;
                    if (locationHistoryView != null) { 
                        locationHistoryView.getItems().clear();
                    }
                }
            }
        );
    }

    @FXML
    private void handleViewLogsClick() {
        // UPDATED: Use the stored incident
        if (currentlySelectedIncident == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident to view its logs.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/LogsView.fxml"));
            Parent root = loader.load();
            LogsViewController controller = loader.getController();
            controller.loadLogs(currentlySelectedIncident.getIncidentId()); // Use the stored ID
            Stage stage = new Stage();
            stage.setTitle("Logs for Incident #" + currentlySelectedIncident.getIncidentId());
            stage.initModality(Modality.NONE);
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(addUnitButton.getScene().getStylesheets().get(0));
            } catch (Exception e) {
                System.err.println("Could not apply stylesheet: " + e.getMessage());
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Could not open logs window.");
        }
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");
            {
                detailsButton.getStyleClass().addAll("button-xs", "flat");
                detailsButton.setOnAction(event -> {
                    ActiveDispatch incident = getTableView().getItems().get(getIndex());
                    if (incident != null) {
                        openDetailsWindow(incident.getIncidentId());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailsButton);
            }
        });
    }

    private void loadInitialData() {
        loadDispatchData();
        loadAllUnits();
    }

    private void setupPolling() {
        Timeline poller = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            loadDispatchData();
            loadAllUnits();
            // Re-select the item after refresh
            if (currentlySelectedIncident != null) {
                activeIncidentsTable.getItems().stream()
                    .filter(item -> item.getIncidentId() == currentlySelectedIncident.getIncidentId())
                    .findFirst()
                    .ifPresent(item -> activeIncidentsTable.getSelectionModel().select(item));
            }
        }));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    private void setupButtonIcons() {
        FontAwesomeIconView plusIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE);
        // plusIcon.setFill(javafx.scene.paint.Color.WHITE); // Uncomment if theme requires it
        newIncidentButton.setGraphic(plusIcon);
    }

    private void displayLocationHistory(ActiveDispatch summary) {
        if (locationHistoryView != null) { // Add null check for safety
            List<LocationHistory> history = incidentDAO.getLocationHistory(summary.getIncidentId());
            locationHistoryView.setItems(FXCollections.observableArrayList(history));
        }
    }

    @FXML
    private void handleAddLocationNoteClick() {
        // UPDATED: Use the stored incident
        String note = newLocationNoteField.getText();
        if (currentlySelectedIncident == null || note == null || note.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident and enter a note.");
            return;
        }
        incidentDAO.addLocationHistory(currentlySelectedIncident.getIncidentId(), note);
        newLocationNoteField.clear();
        displayLocationHistory(currentlySelectedIncident);
    }

    private void openDetailsWindow(int incidentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/IncidentDetailsView.fxml"));
            Parent root = loader.load();
            IncidentDetailsController controller = loader.getController();
            controller.loadIncidentData(incidentId);
            Stage stage = new Stage();
            stage.setTitle("Incident Details - ID: " + incidentId);
            stage.initModality(Modality.NONE);
            Scene scene = new Scene(root);
             try {
                 scene.getStylesheets().add(addUnitButton.getScene().getStylesheets().get(0));
             } catch (Exception e) { System.err.println("Could not apply stylesheet: ".concat(e.getMessage())); }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Could not open details window.");
        }
    }

    @FXML private void handleNewIncidentClick() { openModalForm("/com/emergency/NewIncidentForm.fxml", "Create New Incident"); }
    @FXML private void handleAddUnitClick() { openModalForm("/com/emergency/NewUnitForm.fxml", "Add New Dispatch Unit"); }

    @FXML private void handleDispatchClick() {
        // UPDATED: Use the stored incident
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (currentlySelectedIncident == null || selectedUnit == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident AND an available unit.");
            return;
        }
        if (!"Available".equalsIgnoreCase(selectedUnit.getStatus())) {
             showAlert(Alert.AlertType.WARNING, "Selected unit is not available for dispatch.");
             return;
        }
        incidentDAO.dispatchUnitToIncident(currentlySelectedIncident.getIncidentId(), selectedUnit.getUnitId());
    }

    @FXML private void handleCloseIncidentClick() {
        // UPDATED: Use the stored incident
        if (currentlySelectedIncident == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident to close.");
            return;
        }
        incidentDAO.closeIncident(currentlySelectedIncident.getIncidentId());
    }

    @FXML private void handleSetOnSceneClick() {
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (selectedUnit != null) {
            unitDAO.updateUnitStatus(selectedUnit.getUnitId(), "On Scene");
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a unit to update.");
        }
    }

    @FXML private void handleSetClearClick() {
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (selectedUnit != null) {
            unitDAO.updateUnitStatus(selectedUnit.getUnitId(), "Available");
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a unit to update.");
        }
    }

    private void loadDispatchData() {
        List<ActiveDispatch> dispatchesFromDB = incidentDAO.getActiveDispatches();
        activeIncidentsTable.setItems(FXCollections.observableArrayList(dispatchesFromDB));
    }

    private void loadAllUnits() {
        List<Unit> unitsFromDB = unitDAO.getAllUnits();
        availableUnitsTable.setItems(FXCollections.observableArrayList(unitsFromDB));
    }

    private void openModalForm(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage formStage = new Stage();
            formStage.setTitle(title);
            formStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            try { // Apply theme
                scene.getStylesheets().add(addUnitButton.getScene().getStylesheets().get(0));
            } catch (Exception e) { System.err.println("Could not apply stylesheet: " + e.getMessage()); }
            formStage.setScene(scene);
            formStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}