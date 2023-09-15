package it.vegas.gameframework.exceptions;

import lombok.Setter;
import lombok.ToString;

/**
 * Extended version of Exception that override the
 * error's hash with a new customizable errorID.
 * With GameException errors can be handled by the executors
 * and categorized by number.
 * GameException returns the ErrorId by overriding hashCode() method
 */
@ToString
public class GameException extends Exception {

    @Setter
    protected int errorHash;

    public GameException(int errorHash) {
        super();
        this.errorHash = errorHash;
    }

    public GameException(int errorHash, String message) {
        super(message);
        this.errorHash = errorHash;
    }

    public GameException(int errorHash, String message, Throwable cause) {
        super(message, cause);
        this.errorHash = errorHash;
    }

    @Override
    public String getMessage() {
        return "[Error Id: " + errorHash + "]" + super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return "[Error Id: " + errorHash + "]" + super.getLocalizedMessage();
    }

    @Override
    public int hashCode() {
        return errorHash;
    }
}
