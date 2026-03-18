package hr.java.clientcommunication.exception;

public class UserFileNotFoundException extends RuntimeException {
    public UserFileNotFoundException(String message) {
        super(message);
    }

    public UserFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserFileNotFoundException() {
    }
}
