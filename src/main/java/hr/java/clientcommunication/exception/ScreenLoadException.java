package hr.java.clientcommunication.exception;

public class ScreenLoadException extends RuntimeException {
    public ScreenLoadException(String message) {
        super(message);
    }

  public ScreenLoadException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScreenLoadException(Throwable cause) {
    super(cause);
  }

  public ScreenLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ScreenLoadException() {
  }
}
