module hr.java.clientcommunication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens hr.java.clientcommunication to javafx.fxml;
    exports hr.java.clientcommunication.main;
    opens hr.java.clientcommunication.main to javafx.fxml;
    exports hr.java.clientcommunication.controller;
    opens hr.java.clientcommunication.controller to javafx.fxml;
}