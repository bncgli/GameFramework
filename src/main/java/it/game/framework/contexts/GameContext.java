package it.game.framework.contexts;

import it.game.framework.exceptions.GameException;

import java.util.Map;

/**
 * GameContext contains all the data of the game,
 * the state machine can pickup data status and
 * class references from here.
 */
public interface GameContext {

    <V> void put(String key, V value);

    <V> void putAll(Map<String, V> data);

    <V> V get(String key, Class<V> clazz) throws Exception;

    <V> V get(String key) throws Exception;

    void remove(String key);

    void cleanup();

}

