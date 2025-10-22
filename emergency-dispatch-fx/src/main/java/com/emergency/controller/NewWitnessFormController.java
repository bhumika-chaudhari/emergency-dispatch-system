package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // <-- NEW IMPORT
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

        // --- UPDATED: Added Validation Alert ---
        if (firstName.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "First Name and Phone Number are required.");
            return; // Stop if validation fails
        }

        // --- UPDATED: Check if DAO returns success ---
        boolean success = incidentDAO.addWitness(this.incidentId, firstName, lastName, phone, statement);

        if (success) {
            // Close the form window ONLY if save was successful
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } else {
            // Show an error message to the user if save failed
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save the witness. Please check the console/logs for details.");
        }
    }

    // --- NEW: Helper method for showing alerts ---
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait(); // Show and wait for user to close
    }
}