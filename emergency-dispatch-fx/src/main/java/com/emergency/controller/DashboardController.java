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
    @FXML private TableView<Unit> availableUnitsTable;
    @FXML private TableColumn<Unit, String> unitNameColumn;
    @FXML private TableColumn<Unit, String> unitTypeColumn;
    @FXML private TableColumn<Unit, String> unitStatusColumn;
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
        loadInitialData();
        setupPolling();
        setupButtonIcons();
        actionColumn.setCellFactory(param -> new TableCell<>() {
        private final Button detailsButton = new Button("Details");

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                detailsButton.setOnAction(event -> {
                    // Get the incident for the current row
                    ActiveDispatch incident = getTableView().getItems().get(getIndex());
                    openDetailsWindow(incident.getIncidentId());
                });
                setGraphic(detailsButton);
            }
        }
    });
    }
private void openDetailsWindow(int incidentId) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/IncidentDetailsView.fxml"));
        Parent root = loader.load();

        // Get the controller of the new window
        IncidentDetailsController controller = loader.getController();
        // Call the method to pass the incident ID
        controller.loadIncidentData(incidentId);

        Stage stage = new Stage();
        stage.setTitle("Incident Details");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private void setupTableColumns() {
        incidentIdColumn.setCellValueFactory(new PropertyValueFactory<>("incidentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("locationText"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        assignedUnitColumn.setCellValueFactory(new PropertyValueFactory<>("assignedUnit"));
        unitNameColumn.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        unitStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
        plusIcon.setFill(javafx.scene.paint.Color.WHITE);
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

    @FXML
    private void handleNewIncidentClick() {
        openModalForm("/com/emergency/NewIncidentForm.fxml", "Create New Incident");
    }

    @FXML
    private void handleAddUnitClick() {
        openModalForm("/com/emergency/NewUnitForm.fxml", "Add New Dispatch Unit");
    }

    @FXML
    private void handleDispatchClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (selectedIncident == null || selectedUnit == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident AND an available unit.");
            return;
        }
        incidentDAO.dispatchUnitToIncident(selectedIncident.getIncidentId(), selectedUnit.getUnitId());
    }

    @FXML
    private void handleCloseIncidentClick() {
        ActiveDispatch selectedIncident = activeIncidentsTable.getSelectionModel().getSelectedItem();
        if (selectedIncident == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an incident to close.");
            return;
        }
        incidentDAO.closeIncident(selectedIncident.getIncidentId());
    }

    @FXML
    private void handleSetOnSceneClick() {
        Unit selectedUnit = availableUnitsTable.getSelectionModel().getSelectedItem();
        if (selectedUnit != null) {
            unitDAO.updateUnitStatus(selectedUnit.getUnitId(), "On Scene");
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a unit to update.");
        }
    }

    @FXML
    private void handleSetClearClick() {
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
            formStage.setScene(new Scene(root));
            formStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.show();
    }
}