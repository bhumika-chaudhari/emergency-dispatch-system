package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewWitnessFormController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextArea statementArea;
    @FXML private Button saveButton;

    private IncidentDAO incidentDAO = new IncidentDAO();
    private int incidentId;

    // This method allows the details controller to pass the incident ID to this form
    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    @FXML
    private void handleSaveWitnessClick() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String statement = statementArea.getText();

        if (firstName.isEmpty() || phone.isEmpty()) {
            // Add validation if needed
            return;
        }
        
        // Use the stored incidentId to add the witness
        incidentDAO.addWitness(this.incidentId, firstName, lastName, phone, statement);
        
        // Close the form window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}