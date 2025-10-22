package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.Caller;
import com.emergency.model.Incident;
import com.emergency.model.Witness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class IncidentDetailsController {

    // --- FXML Variables ---
    @FXML private Label incidentIdLabel;
    @FXML private Label typeLabel;
    @FXML private Label locationLabel;     // <-- ADDED
    @FXML private Label priorityLabel;     // <-- ADDED
    @FXML private Label severityLabel;     // <-- ADDED
    @FXML private Label callerNameLabel;
    @FXML private Label callerPhoneLabel;
    @FXML private Label callerEmailLabel;
    @FXML private ListView<Witness> witnessListView;
    @FXML private Button addWitnessButton;

    // --- DAO and State ---
    private IncidentDAO incidentDAO = new IncidentDAO();
    private int currentIncidentId;

    @FXML
    public void initialize() {
        // Setup the custom cell factory for the witness list view
        witnessListView.setCellFactory(lv -> new ListCell<Witness>() {
            private HBox hbox = new HBox(10);
            private Text witnessInfo = new Text();
            private Button deleteButton = new Button("Delete");

            { // Initializer block
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox spacer = new HBox();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                hbox.getChildren().addAll(witnessInfo, spacer, deleteButton);

                deleteButton.getStyleClass().addAll("button-text", "small");
                deleteButton.setStyle("-fx-text-fill: #BF616A; -fx-background-color: transparent;");
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-text-fill: #D08770; -fx-background-color: #3B4252;"));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-text-fill: #BF616A; -fx-background-color: transparent;"));

                deleteButton.setOnAction(event -> {
                    Witness witness = getItem();
                    if (witness != null) {
                        incidentDAO.deleteWitness(witness.getWitnessId());
                        loadWitnesses();
                    }
                });
            }

            @Override
            protected void updateItem(Witness witness, boolean empty) {
                super.updateItem(witness, empty);
                if (empty || witness == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    witnessInfo.setText(witness.toString());
                    witnessInfo.getStyleClass().add("text");
                    setGraphic(hbox);
                }
            }
        });
    }

    // Load data when the window is opened
    public void loadIncidentData(int incidentId) {
        this.currentIncidentId = incidentId;

        // Load incident details and populate all relevant labels
        Incident incident = incidentDAO.getIncidentDetailsById(incidentId);
        if (incident != null) {
            incidentIdLabel.setText(String.valueOf(incident.getId()));
            typeLabel.setText(incident.getType());
            locationLabel.setText(incident.getLocationText()); // <-- POPULATE
            priorityLabel.setText(incident.getPriority());   // <-- POPULATE
            severityLabel.setText(String.valueOf(incident.getSeverityLevel())); // <-- POPULATE
        } else {
            // Clear fields if incident not found
            incidentIdLabel.setText("-");
            typeLabel.setText("-");
            locationLabel.setText("-");
            priorityLabel.setText("-");
            severityLabel.setText("-");
        }

        // Load caller details
        Caller caller = incidentDAO.getCallerDetails(incidentId);
        if (caller != null) {
            callerNameLabel.setText(caller.getFirstName() + " " + caller.getLastName());
            callerPhoneLabel.setText(caller.getPhoneNumber());
            callerEmailLabel.setText(caller.getEmail());
        } else {
            // Clear fields if caller not found
            callerNameLabel.setText("-");
            callerPhoneLabel.setText("-");
            callerEmailLabel.setText("-");
        }

        // Load witnesses
        loadWitnesses();
    }

    // Handle adding a new witness
    @FXML
    private void handleAddWitnessClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/NewWitnessForm.fxml"));
            Parent root = loader.load();

            NewWitnessFormController controller = loader.getController();
            controller.setIncidentId(this.currentIncidentId);

            Stage formStage = new Stage();
            formStage.setTitle("Add New Witness");
            formStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            try { // Apply theme
                scene.getStylesheets().add(addWitnessButton.getScene().getStylesheets().get(0));
            } catch (Exception e) {
                System.err.println("Could not apply stylesheet: " + e.getMessage());
            }
            formStage.setScene(scene);
            formStage.showAndWait();

            loadWitnesses(); // Refresh the list after adding

        } catch (IOException e) {
            e.printStackTrace();
            // Consider adding showAlert here
        }
    }

    // Helper to load/refresh the witness list
    private void loadWitnesses() {
        if (this.currentIncidentId > 0) {
            List<Witness> witnesses = incidentDAO.getWitnesses(this.currentIncidentId);
            witnessListView.setItems(FXCollections.observableArrayList(witnesses));
        } else {
            witnessListView.getItems().clear();
        }
    }

    // Helper method for showing alerts (can be added if needed)
    /*
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    */
}