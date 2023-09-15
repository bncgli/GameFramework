package it.vegas.gameframework.exceptions;

import lombok.Setter;
import lombok.ToString;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return "[Error hash: " + errorHash + "] " + super.getMessage() + "\n" + Stream.of(super.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String getLocalizedMessage() {
        return "[Error hash: " + errorHash + "] " + super.getLocalizedMessage() + "\n" + Stream.of(super.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return errorHash;
    }
}
