package com.emergency.controller;

import com.emergency.dao.UnitDAO;
import com.emergency.model.Location; // <-- NEW IMPORT
import javafx.collections.FXCollections; // <-- NEW IMPORT
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // <-- NEW IMPORT
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox; // <-- NEW IMPORT
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List; // <-- NEW IMPORT

public class NewUnitFormController {

    @FXML private TextField unitNameField;
    @FXML private TextField unitTypeField;
    @FXML private ComboBox<Location> locationComboBox; // <-- ADDED
    @FXML private Button submitButton;

    private UnitDAO unitDAO = new UnitDAO();

    @FXML
    public void initialize() { // <-- ADDED Initialize method
        // Fetch locations from the database
        List<Location> locations = unitDAO.getAllLocations();
        // Populate the ComboBox
        locationComboBox.setItems(FXCollections.observableArrayList(locations));
        // Optionally select the first location by default
        if (!locations.isEmpty()) {
            locationComboBox.setValue(locations.get(0));
        }
    }

    @FXML
    private void handleSubmitButtonClick() {
        String name = unitNameField.getText();
        String type = unitTypeField.getText();
        Location selectedLocation = locationComboBox.getValue(); // <-- Get selected Location

        // Updated Validation
        if (name == null || name.trim().isEmpty() ||
            type == null || type.trim().isEmpty() ||
            selectedLocation == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Unit Name, Type, and Location are required.");
            return;
        }

        // Pass the selected location's ID to the DAO
        boolean success = unitDAO.createNewUnit(name.trim(), type.trim(), selectedLocation.getId());

        if (success) {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save the new unit.");
        }
    }

    // Helper method for showing alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait(); // Show and wait for user to close
    }
}