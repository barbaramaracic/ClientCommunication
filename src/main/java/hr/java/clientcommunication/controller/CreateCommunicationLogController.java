package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.CommunicationLog;
import hr.java.clientcommunication.entity.User;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.ContactType;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import hr.java.clientcommunication.repository.CommunicationLogDatabaseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller responsible for creating and saving a new communication log.
 * Includes user confirmation before saving, and logs all actions and exceptions.
 */
public class CreateCommunicationLogController {

    private static final Logger logger = LoggerFactory.getLogger(CreateCommunicationLogController.class);

    @FXML private ComboBox<String> communicationTypeComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private CheckBox successCheckbox;

    /**
     * Initializes the controller by populating communication types and client list.
     */
    @FXML
    public void initialize() {
        try {
            communicationTypeComboBox.getItems().addAll("EMAIL", "SMS", "PHONE");

            ClientDatabaseRepository clientRepo = new ClientDatabaseRepository();
            List<Client> clients = clientRepo.findAll();
            clientComboBox.getItems().addAll(clients);

            clientComboBox.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.toSimpleString());
                }
            });

            clientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.toSimpleString());
                }
            });

            logger.info("CommunicationLogController initialized successfully.");
        } catch (Exception e) {
            logger.error("Error during initialization: {}", e.getMessage(), e);
            showError("Error while initializing communication log form: " + e.getMessage());
        }
    }

    /**
     * Handles the save button click event.
     * Shows confirmation dialog, builds and saves the communication log.
     */
    @FXML
    private void onSave() {
        if (!validateInput()) {
            return; // prekini ako validacija ne prođe
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to save this communication log?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Save");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    CommunicationLog log = new CommunicationLog();
                    log.setDateTime(java.time.LocalDateTime.now());
                    log.setType(ContactType.valueOf(communicationTypeComboBox.getValue()));
                    log.setNote(descriptionArea.getText());
                    log.setClient(clientComboBox.getValue());
                    log.setSuccess(successCheckbox.isSelected());

                    User currentUser = UserSession.getLoggedInUser();
                    log.setUserResponsible(currentUser);

                    CommunicationLogDatabaseRepository repo = new CommunicationLogDatabaseRepository();
                    repo.save(log);

                    logger.info("Communication log saved successfully: {}", log);

                    MenuController menuController = new MenuController();
                    menuController.showSearchCommunicationLogsScreen();

                } catch (Exception e) {
                    logger.error("Error while saving communication log: {}", e.getMessage(), e);
                    showError("An error occurred while saving the communication log: " + e.getMessage());
                }
            } else {
                logger.info("User cancelled the communication log save operation.");
            }
        });
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (communicationTypeComboBox.getValue() == null || communicationTypeComboBox.getValue().isEmpty()) {
            errors.append("- Please select a communication type.\n");
        }

        if (clientComboBox.getValue() == null) {
            errors.append("- Please select a client.\n");
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            errors.append("- Description cannot be empty.\n");
        }

        if (description.length() > 500) {
            errors.append("- Description is too long. Maximum allowed characters: 500.\n");
        }

        if (errors.length() > 0) {
            showError("Please fix the following errors:\n" + errors.toString());
            return false;
        }

        return true;
    }


    /**
     * Displays an error alert with a given message.
     *
     * @param message the message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Could not save communication log");
        alert.showAndWait();
    }
}
