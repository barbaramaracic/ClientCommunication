package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.Client;
import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.exception.RepositoryAccessException;
import hr.java.clientcommunication.repository.ClientDatabaseRepository;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SearchClientsController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SearchClientsController.class);
    @FXML
    private TextField clientFirstNameTextField;

    @FXML
    private TextField clientLastNameTextField;

    @FXML
    private TextField clientEmailTextField;

    @FXML
    private TextField clientPhoneTextField;

    @FXML
    private CheckBox emailAllowedFilterCheckBox;

    @FXML
    private CheckBox smsAllowedFilterCheckBox;

    @FXML
    private CheckBox phoneCallAllowedFilterCheckBox;

    @FXML
    private TableView<Client> clientTableView;

    @FXML
    private TableColumn<Client, String> clientIdColumn;

    @FXML
    private TableColumn<Client, String> clientFirstNameColumn;

    @FXML
    private TableColumn<Client, String> clientLastNameColumn;

    @FXML
    private TableColumn<Client, String> clientEmailColumn;

    @FXML
    private TableColumn<Client, String> clientPhoneColumn;

    @FXML
    private TableColumn<Client, Boolean> emailAllowedColumn;

    @FXML
    private TableColumn<Client, Boolean> smsAllowedColumn;

    @FXML
    private TableColumn<Client, Boolean> phoneCallAllowedColumn;

    @FXML
    private TableColumn<Client, Void> actionColumn;

    private final ClientDatabaseRepository clientRepository = new ClientDatabaseRepository();

    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
    public void initialize() {
        clientIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        clientFirstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));

        clientLastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));

        clientEmailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        clientPhoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPhone()));

        emailAllowedColumn.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(
                        cellData.getValue().getPreference() != null &&
                                cellData.getValue().getPreference().isEmailAllowed()));

        smsAllowedColumn.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(
                        cellData.getValue().getPreference() != null &&
                                cellData.getValue().getPreference().isSmsAllowed()));

        phoneCallAllowedColumn.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(
                        cellData.getValue().getPreference() != null &&
                                cellData.getValue().getPreference().isPhoneCallAllowed()));

        emailAllowedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(emailAllowedColumn));
        smsAllowedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(smsAllowedColumn));
        phoneCallAllowedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(phoneCallAllowedColumn));

        // Postavi akcijske gumbe
        setActionButtons();

        loadAllClients();
    }

    private void loadAllClients() {
        try {
            List<Client> allClients = clientRepository.findAll();
            ObservableList<Client> observableClients = FXCollections.observableList(allClients);
            clientTableView.setItems(observableClients);
        } catch (RepositoryAccessException e) {
            logger.error( "Failed to load clients", e);
            showError("Failed to load clients.");
        }
    }

    public void filterClients() {
        try {
            List<Client> clients = clientRepository.findAll();

            String firstName = clientFirstNameTextField.getText().toLowerCase();
            String lastName = clientLastNameTextField.getText().toLowerCase();
            String email = clientEmailTextField.getText().toLowerCase();
            String phone = clientPhoneTextField.getText().toLowerCase();

            if (!firstName.isEmpty()) {
                clients = clients.stream()
                        .filter(c -> c.getFirstName().toLowerCase().contains(firstName))
                        .toList();
            }

            if (!lastName.isEmpty()) {
                clients = clients.stream()
                        .filter(c -> c.getLastName().toLowerCase().contains(lastName))
                        .toList();
            }

            if (!email.isEmpty()) {
                clients = clients.stream()
                        .filter(c -> c.getEmail().toLowerCase().contains(email))
                        .toList();
            }

            if (!phone.isEmpty()) {
                clients = clients.stream()
                        .filter(c -> c.getPhone().toLowerCase().contains(phone))
                        .toList();
            }

            if (emailAllowedFilterCheckBox.isSelected()) {
                clients = clients.stream()
                        .filter(c -> c.getPreference() != null && c.getPreference().isEmailAllowed())
                        .toList();
            }

            if (smsAllowedFilterCheckBox.isSelected()) {
                clients = clients.stream()
                        .filter(c -> c.getPreference() != null && c.getPreference().isSmsAllowed())
                        .toList();
            }

            if (phoneCallAllowedFilterCheckBox.isSelected()) {
                clients = clients.stream()
                        .filter(c -> c.getPreference() != null && c.getPreference().isPhoneCallAllowed())
                        .toList();
            }

            clientTableView.setItems(FXCollections.observableList(clients));

        } catch (RepositoryAccessException e) {
            logger.error("Failed to filter clients", e);
            showError("Failed to filter clients.");
        }
    }

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

    private void handleDeleteAction(Client client) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Client Delete");
        alert.setHeaderText("Delete Confirmation");
        alert.setContentText("Do you want to delete this client?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clientRepository.deleteById(client.getId());
                    clientTableView.getItems().remove(client);
                    showInfo("Client deleted successfully.");
                } catch (RepositoryAccessException e) {
                    showError("Client deletion failed.");
                }
            }
        });
    }

    private void handleUpdateAction(Client client) {
        try {
            UpdateClientsController updateController = menuController.showUpdateClientsScreen();
            updateController.setClient(client);
        } catch (IOException e) {
            logger.error("Failed to handle update", e);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}