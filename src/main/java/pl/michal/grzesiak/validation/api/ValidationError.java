package pl.michal.grzesiak.validation.api;

public class ValidationError {

    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "message='" + message + '\'' +
                '}';
    }
}
