package hr.java.clientcommunication.exception;

public class DatabaseAccessInterruptedException extends Exception {
    public DatabaseAccessInterruptedException() {
    }

    public DatabaseAccessInterruptedException(String message) {
        super(message);
    }

    public DatabaseAccessInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseAccessInterruptedException(Throwable cause) {
        super(cause);
    }

    public DatabaseAccessInterruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
