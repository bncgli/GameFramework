package it.game.framework.contexts;


import org.springframework.stereotype.Component;

import java.io.Serializable;



/**
 * GameContext contains all the data of the game,
 * the state machine can pickup data status and
 * class references from here.
 */
@Component
public class GameContext implements Serializable  {

}
