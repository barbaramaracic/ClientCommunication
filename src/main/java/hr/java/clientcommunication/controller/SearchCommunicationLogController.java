package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.CommunicationLog;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.ContactType;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import hr.java.clientcommunication.repository.CommunicationLogDatabaseRepository;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for searching and managing communication logs.
 * Enables filtering, viewing, updating, and deleting communication records.
 */
public class SearchCommunicationLogController {

    private static final Logger logger = LoggerFactory.getLogger(SearchCommunicationLogController.class);

    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<ContactType> contactTypeComboBox;
    @FXML private DatePicker dateFromPicker;
    @FXML private DatePicker dateToPicker;
    @FXML private TextField usernameTextField;
    @FXML private CheckBox successCheckBox;
    @FXML private TextArea noteTextArea;

    @FXML private TableView<CommunicationLog> logTableView;
    @FXML private TableColumn<CommunicationLog, String> clientColumn;
    @FXML private TableColumn<CommunicationLog, String> typeColumn;
    @FXML private TableColumn<CommunicationLog, String> dateTimeColumn;
    @FXML private TableColumn<CommunicationLog, String> userColumn;
    @FXML private TableColumn<CommunicationLog, Boolean> successColumn;
    @FXML private TableColumn<CommunicationLog, String> noteColumn;
    @FXML private TableColumn<CommunicationLog, Void> actionColumn;

    private final CommunicationLogDatabaseRepository logRepository = new CommunicationLogDatabaseRepository();
    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();
    private MenuController menuController;

    /**
     * Injects the MenuController for screen navigation.
     * @param menuController the controller managing screen transitions
     */
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    /**
     * Initializes the controller by configuring table columns,
     * loading data, and setting up action buttons.
     */
    public void initialize() {
        clientColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClient().getFullName()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType().toString()));
        dateTimeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateTime().toString()));
        userColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUserResponsible().getUsername()));
        successColumn.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isSuccess()));
        successColumn.setCellFactory(CheckBoxTableCell.forTableColumn(successColumn));
        noteColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNote()));
        contactTypeComboBox.setItems(FXCollections.observableArrayList(ContactType.values()));

        loadClients();
        loadLogs();
        setActionButtons();
    }

    /**
     * Loads all clients from the repository and populates the combo box.
     */
    private void loadClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            clientComboBox.setItems(FXCollections.observableArrayList(clients));
        } catch (RepositoryAccessException e) {
            logger.error("Failed to load clients from the database.", e);
            showError("Failed to load clients.");
        }
    }

    /**
     * Loads all communication logs and populates the table view.
     */
    private void loadLogs() {
        try {
            List<CommunicationLog> logs = logRepository.findAll();
            logTableView.setItems(FXCollections.observableArrayList(logs));
        } catch (RepositoryAccessException e) {
            logger.error("Failed to load communication logs.", e);
            showError("Failed to load communication logs.");
        }
    }

    /**
     * Filters the communication logs based on selected criteria.
     */
    public void filterLogs() {
        try {
            List<CommunicationLog> logs = logRepository.findAll();

            Client selectedClient = clientComboBox.getValue();
            ContactType selectedType = contactTypeComboBox.getValue();
            boolean successOnly = successCheckBox.isSelected();
            String noteSearch = noteTextArea.getText().toLowerCase().trim();
            LocalDate dateFrom = dateFromPicker.getValue();
            LocalDate dateTo = dateToPicker.getValue();
            String username = usernameTextField.getText().toLowerCase().trim();

            if (selectedClient != null) {
                logs = logs.stream().filter(log -> log.getClient().equals(selectedClient)).toList();
            }
            if (selectedType != null) {
                logs = logs.stream().filter(log -> log.getType().equals(selectedType)).toList();
            }
            if (successOnly) {
                logs = logs.stream().filter(CommunicationLog::isSuccess).toList();
            }
            if (!noteSearch.isEmpty()) {
                logs = logs.stream()
                        .filter(log -> log.getNote() != null && log.getNote().toLowerCase().contains(noteSearch))
                        .toList();
            }
            if (dateFrom != null) {
                LocalDateTime fromDateTime = dateFrom.atStartOfDay();
                logs = logs.stream().filter(log -> !log.getDateTime().isBefore(fromDateTime)).toList();
            }
            if (dateTo != null) {
                LocalDateTime toDateTime = dateTo.atTime(LocalTime.MAX);
                logs = logs.stream().filter(log -> !log.getDateTime().isAfter(toDateTime)).toList();
            }
            if (username != null && !username.isEmpty()) {
                logs = logs.stream()
                        .filter(log -> log.getUserResponsible().getUsername().toLowerCase().contains(username))
                        .toList();
            }

            logTableView.setItems(FXCollections.observableArrayList(logs));

        } catch (RepositoryAccessException e) {
            logger.error("Error occurred while filtering communication logs.", e);
            showError("Error while filtering communication logs.");
        }
    }

    /**
     * Sets up action buttons (Update, Delete) in the table's action column.
     */
    private void setActionButtons() {
        if (!UserSession.isAdmin()) {
            actionColumn.setVisible(false);
            return;
        }

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = createDeleteButton();
            private final Button updateButton = createUpdateButton();
            private final HBox actionButtons = new HBox(5, updateButton, deleteButton);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionButtons);
            }

            private Button createDeleteButton() {
                Button button = new Button("Delete");
                button.setOnAction(event -> handleDeleteAction(getTableView().getItems().get(getIndex())));
                return button;
            }

            private Button createUpdateButton() {
                Button button = new Button("Update");
                button.setOnAction(event -> handleUpdateAction(getTableView().getItems().get(getIndex())));
                return button;
            }
        });
    }

    /**
     * Handles the delete operation for a communication log entry.
     * @param log the communication log to delete
     */
    private void handleDeleteAction(CommunicationLog log) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Entry");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete this communication log?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    logRepository.deleteById(log.getId());
                    logTableView.getItems().remove(log);
                    showInfo("Communication log deleted successfully.");
                } catch (RepositoryAccessException e) {
                    logger.error("Failed to delete communication log with ID {}", log.getId(), e);
                    showError("Failed to delete communication log.");
                }
            }
        });
    }

    /**
     * Handles the update operation by showing the update screen for the selected log.
     * @param log the communication log to update
     */
    private void handleUpdateAction(CommunicationLog log) {
        try {
            UpdateCommunicationLogController updateController = menuController.showUpdateCommunicationLogScreen();
            updateController.setCommunicationLog(log);
        } catch (IOException e) {
            logger.error("Failed to load update screen for communication log ID {}", log.getId(), e);
        }
    }

    /**
     * Displays an error alert with the specified message.
     * @param msg the error message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Displays an informational alert with the specified message.
     * @param msg the info message to display
     */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
