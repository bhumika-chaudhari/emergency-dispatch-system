package com.emergency.controller;

import com.emergency.dao.UnitDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewUnitFormController {

    @FXML private TextField unitNameField;
    @FXML private TextField unitTypeField;
    @FXML private Button submitButton;

    private UnitDAO unitDAO = new UnitDAO();

    @FXML
    private void handleSubmitButtonClick() {
        String name = unitNameField.getText();
        String type = unitTypeField.getText();

        if (name.isEmpty() || type.isEmpty()) {
            // Simple validation
            return;
        }
        
        unitDAO.createNewUnit(name, type);
        
        // Close the form window
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}