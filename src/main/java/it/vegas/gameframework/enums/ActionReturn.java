package it.vegas.gameframework.enums;

import lombok.Getter;

/**
 * List of status returned by the execute method of IState
 */
@Getter
public enum ActionReturn {
    OK(0),
    ERROR(100);

    final int id;

    ActionReturn(int id) {
        this.id = id;
    }

}
