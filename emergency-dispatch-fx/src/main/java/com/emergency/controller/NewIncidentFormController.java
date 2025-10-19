package com.emergency.controller;

import com.emergency.dao.IncidentDAO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewIncidentFormController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField typeField;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitButton;

    // NEW FXML VARIABLES
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<Integer> severityComboBox;

    private IncidentDAO incidentDAO = new IncidentDAO();

    // NEW METHOD: This runs when the form is opened
    @FXML
    public void initialize() {
        // Populate the dropdown menus
        priorityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));
        severityComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        // Set a default selection
        priorityComboBox.setValue("Medium");
        severityComboBox.setValue(3);
    }

    @FXML
    private void handleSubmitButtonClick() {
        // Get data from all fields, including the new dropdowns
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        String type = typeField.getText();
        String location = locationField.getText();
        String description = descriptionArea.getText();
        String priority = priorityComboBox.getValue();
        int severity = severityComboBox.getValue();

        // Call the updated DAO method
        incidentDAO.createNewIncident(firstName, lastName, phone, type, description, location, priority, severity);
        
        // Close the form window
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}