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

    @FXML private Label incidentIdLabel;
    @FXML private Label typeLabel;
    @FXML private Label locationLabel;    
    @FXML private Label priorityLabel;     
    @FXML private Label severityLabel;     
    @FXML private Label callerNameLabel;
    @FXML private Label callerPhoneLabel;
    @FXML private ListView<Witness> witnessListView;
    @FXML private Button addWitnessButton;

    // --- DAO and State ---
    private IncidentDAO incidentDAO = new IncidentDAO();
    private int currentIncidentId;

    @FXML
    public void initialize() {
        witnessListView.setCellFactory(lv -> new ListCell<Witness>() {
            private HBox hbox = new HBox(10);
            private Text witnessInfo = new Text();
            private Button deleteButton = new Button("Delete");

            { 
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

    public void loadIncidentData(int incidentId) {
        this.currentIncidentId = incidentId;

        Incident incident = incidentDAO.getIncidentDetailsById(incidentId);
        if (incident != null) {
            incidentIdLabel.setText(String.valueOf(incident.getId()));
            typeLabel.setText(incident.getType());
            locationLabel.setText(incident.getLocationText()); 
            priorityLabel.setText(incident.getPriority());   
            severityLabel.setText(String.valueOf(incident.getSeverityLevel())); 
        } else {
            incidentIdLabel.setText("-");
            typeLabel.setText("-");
            locationLabel.setText("-");
            priorityLabel.setText("-");
            severityLabel.setText("-");
        }

        Caller caller = incidentDAO.getCallerDetails(incidentId);
        if (caller != null) {
            callerNameLabel.setText(caller.getFirstName() + " " + caller.getLastName());
            callerPhoneLabel.setText(caller.getPhoneNumber());
          
        } else {
            callerNameLabel.setText("-");
            callerPhoneLabel.setText("-");
        }

        loadWitnesses();
    }

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
           
        }
    }

    private void loadWitnesses() {
        if (this.currentIncidentId > 0) {
            List<Witness> witnesses = incidentDAO.getWitnesses(this.currentIncidentId);
            witnessListView.setItems(FXCollections.observableArrayList(witnesses));
        } else {
            witnessListView.getItems().clear();
        }
    }

   
}