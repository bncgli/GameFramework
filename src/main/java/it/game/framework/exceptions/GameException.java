package it.game.framework.exceptions;

import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
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
public class GameException extends Exception implements Serializable {

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

    /**
     * Formats an exception inserting contestualizing it with the gamestate that throwed it
     *
     * @param e             the exception to be formatted
     * @param gameStateName The gamestate that throwed it
     * @return the string of the formatted exception
     */
    public static String format(Exception e, String gameStateName) {
        return "[Error hash: " + e.hashCode() + " in " + gameStateName + "] " + e.getMessage() + "\n" + Stream.of(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    /**
     * Formats an exception to be readable with code and message
     *
     * @param e The exception to be formatted
     * @return the string of the formatted exception
     */
    public static String format(Exception e) {
        return "[Error hash: " + e.hashCode() + "] " + e.getMessage() + "\n" + Stream.of(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    /**
     * Overrides the hash of the error
     * to return the custom errorhash
     *
     * @return the custom errorhash of the gameException
     */
    @Override
    public int hashCode() {
        return errorHash;
    }
}
