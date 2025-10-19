package com.emergency.controller;

import com.emergency.dao.IncidentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewIncidentFormController {

    // These variables link to the fx:id's in the FXML file
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField typeField;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitButton;

    private IncidentDAO incidentDAO = new IncidentDAO();

    // This method is called when the "Submit" button is clicked
    @FXML
    private void handleSubmitButtonClick() {
        // 1. Get the text from all the form fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String type = typeField.getText();
        String description = descriptionArea.getText();

        // 2. Call the DAO method to save the data to the database
        int newId = incidentDAO.createNewIncident(firstName, lastName, phone, type, description, ""); // Location text is empty for now
        
        System.out.println("New incident created with ID: " + newId);
        
        // 3. Close the form window
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}