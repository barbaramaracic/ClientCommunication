package hr.java.clientcommunication.interfaces;

/**
 * Interface representing an entity whose details can be logged.
 */
public interface Loggable {

    /**
     * Returns details that can be displayed in a log or communication record.
     *
     * @return a textual description of the log entry
     */
    String getLogDetails();
}
