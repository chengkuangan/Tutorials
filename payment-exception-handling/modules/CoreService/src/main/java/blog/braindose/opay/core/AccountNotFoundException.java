package blog.braindose.opay.core;

/**
 * Account Not Found exception
 */
public class AccountNotFoundException extends RuntimeException{
    public final String message;

    public AccountNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
