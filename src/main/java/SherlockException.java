/**
 * Represents an error caused by an invalid Sherlock command or command argument.
 */
class SherlockException extends Exception {
    SherlockException(String message) {
        super(message);
    }
}
