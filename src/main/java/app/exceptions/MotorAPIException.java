package app.exceptions;

public class MotorAPIException extends RuntimeException {
    public MotorAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
