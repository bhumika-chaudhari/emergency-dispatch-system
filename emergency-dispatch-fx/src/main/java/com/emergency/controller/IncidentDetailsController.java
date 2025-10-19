package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import com.emergency.model.Caller;
import com.emergency.model.Incident;
import com.emergency.model.Witness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class IncidentDetailsController {

    @FXML private Label incidentIdLabel;
    @FXML private Label typeLabel;
    @FXML private Label callerNameLabel;
    @FXML private Label callerPhoneLabel;
  
    @FXML private ListView<Witness> witnessListView;
    @FXML private Button addWitnessButton;

    private IncidentDAO incidentDAO = new IncidentDAO();
    private int currentIncidentId;

    // This method is called from the main dashboard to pass the incident ID
    public void loadIncidentData(int incidentId) {
        this.currentIncidentId = incidentId; // Store the ID for later use

        // Load incident details
        Incident incident = incidentDAO.getIncidentDetailsById(incidentId);
        if (incident != null) {
            incidentIdLabel.setText(String.valueOf(incident.getId()));
            typeLabel.setText(incident.getType());
        }

        // Load caller details for the first tab
        Caller caller = incidentDAO.getCallerDetails(incidentId);
        if (caller != null) {
            callerNameLabel.setText(caller.getFirstName() + " " + caller.getLastName());
            callerPhoneLabel.setText(caller.getPhoneNumber());
            
        }

        // Load the initial list of witnesses
        loadWitnesses();
    }

    // New handler for the "Add New Witness" button
    @FXML
    private void handleAddWitnessClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/emergency/NewWitnessForm.fxml"));
            Parent root = loader.load();

            // Get the controller of the new form
            NewWitnessFormController controller = loader.getController();
            // Pass the current incident's ID to the form
            controller.setIncidentId(this.currentIncidentId);

            Stage formStage = new Stage();
            formStage.setTitle("Add New Witness");
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setScene(new Scene(root));
            
            // Show the form and wait for it to be closed
            formStage.showAndWait();
            
            // After the form is closed, refresh the witness list
            loadWitnesses();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to refresh the witness list
    private void loadWitnesses() {
        if (this.currentIncidentId > 0) {
            List<Witness> witnesses = incidentDAO.getWitnesses(this.currentIncidentId);
            witnessListView.setItems(FXCollections.observableArrayList(witnesses));
        }
    }
}