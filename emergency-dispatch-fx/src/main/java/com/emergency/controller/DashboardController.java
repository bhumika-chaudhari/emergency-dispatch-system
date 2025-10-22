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
                    displayLocationHistory(newSelection);
                } else {
                    locationHistoryView.getItems().clear();
                }
            }
        );
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");
            {
                detailsButton.getStyleClass().addAll("button-xs", "flat"); // Optional styling
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
        }));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    private void setupButtonIcons() {
        FontAwesomeIconView plusIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE);
        // plusIcon.setFill(javafx.scene.paint.Color.WHITE); // Uncomment if needed
        newIncidentButton.setGraphic(plusIcon);
    }

    private void displayLocationHistory(ActiveDispatch summary) {
        List<LocationHistory> history = incidentDAO.getLocationHistory(summary.getIncidentId());
        locationHistoryView.setItems(FXCollections.observableArrayList(history));
    }

    @FXML
    private void handleAddLocationNoteClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        String note = newLocationNoteField.getText();
        if (selectedIncident == null || note == null || note.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident and enter a note.");
            return;
        }
        incidentDAO.addLocationHistory(selectedIncident.getIncidentId(), note);
        newLocationNoteField.clear();
        displayLocationHistory(selectedIncident);
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
             try { // Apply theme
                 scene.getStylesheets().add(addUnitButton.getScene().getStylesheets().get(0));
             } catch (Exception e) { System.err.println("Could not apply stylesheet: " + e.getMessage()); }
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
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (selectedIncident == null || selectedUnit == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident AND an available unit.");
            return;
        }
        if (!"Available".equalsIgnoreCase(selectedUnit.getStatus())) {
             showAlert(Alert.AlertType.WARNING, "Selected unit is not available for dispatch.");
             return;
        }
        incidentDAO.dispatchUnitToIncident(selectedIncident.getIncidentId(), selectedUnit.getUnitId());
    }

    @FXML private void handleCloseIncidentClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        if (selectedIncident == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident to close.");
            return;
        }
        incidentDAO.closeIncident(selectedIncident.getIncidentId());
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
        // Decide if you want ALL units or only AVAILABLE ones in this table
        List<Unit> unitsFromDB = unitDAO.getAllUnits();
        // List<Unit> unitsFromDB = unitDAO.getAvailableUnits(); // Use this if you want only available units
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
        alert.show(); // Use show() for non-blocking alerts
    }
}