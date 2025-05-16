package service.exception;

public class InMemoryTaskManagerLoadException extends RuntimeException {
    public InMemoryTaskManagerLoadException(String message) {
        super(message);
    }
}
