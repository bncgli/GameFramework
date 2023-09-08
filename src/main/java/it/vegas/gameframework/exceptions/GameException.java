package it.vegas.gameframework.exceptions;

import lombok.Setter;
import lombok.ToString;

@ToString
public class GameException extends Exception {

    @Setter
    protected int errorId;

    public GameException(int errorId) {
        super();
        this.errorId = errorId;
    }

    public GameException(int errorId, String message) {
        super(message);
        this.errorId = errorId;
    }

    public GameException(int errorId, String message, Throwable cause) {
        super(message, cause);
        this.errorId = errorId;
    }

    @Override
    public String getMessage() {
        return "[Error Id: " + errorId + "]" + super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return "[Error Id: " + errorId + "]" + super.getLocalizedMessage();
    }

    @Override
    public int hashCode() {
        return errorId;
    }
}
