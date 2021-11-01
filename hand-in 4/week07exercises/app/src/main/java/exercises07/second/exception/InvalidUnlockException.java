package exercises07.second.exception;

public class InvalidUnlockException extends RuntimeException {
    public InvalidUnlockException(String msg) {
        super(msg);
    }

}
