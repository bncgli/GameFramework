package it.game.framework.contexts;

import it.game.framework.exceptions.GameException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * GameContext contains all the data of the game,
 * the state machine can pickup data status and
 * class references from here.
 * <br>The GameContext is an
 * <b>Interface</b> that grant the possibility to implement
 * this structure as the user prefers, like implement
 * it in Redis or in Guava or some other structure that
 * can be used as cache. The given function are similar to
 * the ones contained in the Map class.
 */
@Slf4j
public abstract class GameContext {

    public abstract boolean contains(String key);

    /**
     * This method adds an entry of a key-value pair
     * @param key The key to index the saved value
     * @param value The saved value
     * @param <V> the class of the saved value
     */
    public abstract <V> void put(String key, V value);

    /**
     * This method adds all the entries of a map to the
     * GameContext.
     * @param data A map of data
     */
    public abstract void putAll(Map<String, Object> data);

    /**
     * This method returns the element from the GameContext
     * casted into the class passed as parameter
     * @param key The index of the variable needed
     * @param clazz The class we want to return the data into
     * @param <V> The type of the variable
     * @return The requested variable
     * @throws Exception Casting exceptions
     */
    public abstract <V> V get(String key, Class<V> clazz) throws Exception;

    /**
     * This method returns the element from the GameContext
     * uncasted, to cast it use:<br><br>
     *  {@code context.<Integer>get(myInteger)}<br>
     *  or<br>
     *  {@code (Integer) context.get(myInteger)}
     * @param key The index of the variable needed
     * @param <V> The type of the variable
     * @return The requested variable
     * @throws Exception Casting exceptions
     */
    public abstract <V> V get(String key) throws Exception;

    /**
     * This method removes an element with the indexed under key
     * @param key The element to remove
     */
    public abstract void remove(String key);

    /**
     * This method resets the GameContext purging all the data inside
     */
    public abstract void cleanup();

    /**
     * <u>This method is experimental</u><br>
     * This method gets a dao instance as argument, the argument dao
     * has to be the dao of the data we want from the GameContext.
     * This method process the argument dao by:<br>
     * <ul>
     *     <li>Lists all the filed of the dao with reflection, leaving warnings if field are not found or if are not convetible</li>
     *     <li>Tries to populate the instance with data from the GameContext with the same name</li>
     *     <li>Returns the instance</li>
     * </ul>
     * The method populates only variables contained inside the
     * parameter dao, and not hereditary ones. Also support better
     * classes than primitive types.
     *
     * @param instance The dao class where store the files
     * @param <V>      The type of the dao class
     * @return An instance of the populated dao class
     */
    public <V> V mapTo(V instance) {
        log.info("Starting to map GameContext into class {}", instance.getClass().getSimpleName());
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (contains(f.getName())) {
                try {
                    f.setAccessible(true);
                    var data = get(f.getName(), f.getType());
                    f.set(instance, data);
                    log.info("Field {}:{} popultated with: {}", f.getName(), f.getType().getSimpleName(), data);
                } catch (IllegalArgumentException e) {
                    log.error(GameException.format(e, String.format("Field %s:%s cannot be populated, incompatible field type", f.getName(), f.getType().getSimpleName())));
                } catch (Exception e) {
                    log.error(GameException.format(e));
                }
            } else {
                log.warn("Field {} is not inside the GameContext", f.getName());
            }
        }
        log.info("Finish to map GameContext into class {}", instance.getClass().getSimpleName());
        return instance;
    }

}

