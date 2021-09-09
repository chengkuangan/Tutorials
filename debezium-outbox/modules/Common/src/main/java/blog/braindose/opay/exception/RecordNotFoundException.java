package blog.braindose.opay.exception;

/**
 * Indicates the record is not found in the database.
 */
public class RecordNotFoundException extends RuntimeException{
    public final String message;

    /**
     * Throws this exception when record is not found in the database
     * @param message Error messages
     */
    public RecordNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
