package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.CommunicationLog;
import hr.java.clientcommunication.enumeration.ContactType;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import hr.java.clientcommunication.repository.CommunicationLogDatabaseRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class UpdateCommunicationLogController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdateCommunicationLogController.class);

    @FXML
    private ComboBox<Client> clientComboBox;

    @FXML
    private ComboBox<ContactType> communicationTypeComboBox;

    @FXML
    private CheckBox successCheckbox;

    @FXML
    private TextArea descriptionArea;

    private CommunicationLog communicationLog;

    public void setCommunicationLog(CommunicationLog communicationLog) {
        this.communicationLog = communicationLog;

        // Popuni ComboBox-e
        loadClients();
        communicationTypeComboBox.setItems(FXCollections.observableArrayList(ContactType.values()));

        // Postavi vrijednosti u formu
        clientComboBox.setValue(communicationLog.getClient());
        communicationTypeComboBox.setValue(communicationLog.getType());
        successCheckbox.setSelected(communicationLog.isSuccess());
        descriptionArea.setText(communicationLog.getNote());
    }

    private void loadClients() {
        try {
            List<Client> clients = new ClientDatabaseRepository().findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
        } catch (Exception e) {
            logger.error("Failed to load clients", e);
            showError("Failed to load clients.");
        }
    }

    @FXML
    private void onSave() throws IOException {
        StringBuilder errors = new StringBuilder();

        Client selectedClient = clientComboBox.getValue();
        ContactType selectedType = communicationTypeComboBox.getValue();
        String description = descriptionArea.getText().trim();

        if (selectedClient == null) {
            errors.append("- Please select a client.\n");
        }

        if (selectedType == null) {
            errors.append("- Please select a communication type.\n");
        }

        if (description.isEmpty()) {
            errors.append("- Description cannot be empty.\n");
        } else if (description.length() > 500) { // ili neka druga željena granica
            errors.append("- Description is too long. Maximum allowed characters: 500.\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Update Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to update communication log data?");
        confirmationAlert.setContentText("Click OK to confirm or Cancel to go back.");

        if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            communicationLog.setClient(selectedClient);
            communicationLog.setType(selectedType);
            communicationLog.setSuccess(successCheckbox.isSelected());
            communicationLog.setNote(description);

            try {
                new CommunicationLogDatabaseRepository().update(communicationLog);
            } catch (Exception e) {
                logger.error("Failed to update communication log", e);
                showError("Failed to update communication log.");
            }
            MenuController menuController = new MenuController();
            menuController.showSearchCommunicationLogsScreen();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) clientComboBox.getScene().getWindow();
        stage.close();
    }


}
