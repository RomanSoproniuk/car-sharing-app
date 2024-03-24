package mainpackage.carsharingapp.exceptions;

public class RoleRepeatException extends RuntimeException {
    public RoleRepeatException(String message) {
        super(message);
    }
}
