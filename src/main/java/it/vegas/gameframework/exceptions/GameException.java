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

    public static String format(Exception e){
        return "[Error hash: " + e.hashCode() + "] " + e.getMessage() + "\n" + Stream.of(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return errorHash;
    }
}
