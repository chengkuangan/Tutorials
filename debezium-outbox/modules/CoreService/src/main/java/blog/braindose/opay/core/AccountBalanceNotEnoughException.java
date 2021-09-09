package blog.braindose.opay.core;

/**
 * Account Balance is not enough exception
 */
public class AccountBalanceNotEnoughException extends RuntimeException{
    public final String message;

    public AccountBalanceNotEnoughException(String message) {
        super(message);
        this.message = message;
    }
}
