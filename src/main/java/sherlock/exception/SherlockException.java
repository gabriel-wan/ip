package sherlock.exception;

/**
 * Represents an error caused by an invalid Sherlock command or command argument.
 */
public class SherlockException extends Exception {
    public SherlockException(String message) {
        super(message);
    }
}
