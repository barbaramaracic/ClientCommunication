package hr.java.clientcommunication.controller;

import hr.java.clientcommunication.entity.UserSession;
import hr.java.clientcommunication.enumeration.UserRole;
import hr.java.clientcommunication.record.ChangeLog;
import hr.java.clientcommunication.utils.ChangeManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller class for displaying a list of user changes in a table.
 * Loads change logs and displays them with their metadata.
 */
public class ChangesController {

    private static final Logger logger = LoggerFactory.getLogger(ChangesController.class);

    @FXML
    private TableView<ChangeLog> changesTableView;
    //@FXML
    //private TableColumn<ChangeLog, String> changeTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> dateTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> userTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> roleTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> oldTableColumn;
    @FXML
    private TableColumn<ChangeLog, String> newTableColumn;


    /**
     * Initializes the controller by loading user change logs and setting up the table.
     * Catches and logs any exceptions that may occur during initialization.
     */
    @FXML
    public void initialize() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                List<ChangeLog> changes = ChangeManager.loadUserActions();
                ObservableList<ChangeLog> changesObsList = FXCollections.observableArrayList(changes);

                // UI update mora ići na JavaFX Application Thread!
                javafx.application.Platform.runLater(() -> {
                    changesTableView.setItems(changesObsList);
                    setupTable();
                    logger.info("Changes loaded successfully (from thread).");
                });
            } catch (Exception e) {
                logger.error("Error initializing ChangesController in thread: {}", e.getMessage(), e);
            }
        });
        executor.shutdown();
    }

    /**
     * Sets up cell value factories for each column in the table view.
     * Catches and logs any exceptions that may occur during table setup.
     */
    public void setupTable() {
        try {
            //changeTableColumn.setCellValueFactory(cellData ->
                  //  new ReadOnlyStringWrapper(cellData.getValue().toString()));

            dateTableColumn.setCellValueFactory(cellData ->
                    new ReadOnlyStringWrapper(cellData.getValue().timestamp().toString()));

            userTableColumn.setCellValueFactory(cellData ->
                    new ReadOnlyStringWrapper(cellData.getValue().user()));

            roleTableColumn.setCellValueFactory(cellData ->
                    new ReadOnlyStringWrapper(cellData.getValue().role()));

            oldTableColumn.setCellValueFactory(cellData -> {
                String oldValue = cellData.getValue().oldValue();
                return new ReadOnlyStringWrapper(oldValue != null ? oldValue : "");
            });

            newTableColumn.setCellValueFactory(cellData -> {
                String newValue = cellData.getValue().newValue();
                return new ReadOnlyStringWrapper(newValue != null ? newValue : "");
            });

            logger.info("Table setup completed.");
        } catch (Exception e) {
            logger.error("Error setting up the table in ChangesController: {}", e.getMessage(), e);
        }
    }
}
