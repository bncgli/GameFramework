package it.game.framework.serializations;

import it.game.framework.exceptions.GameException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * The Deserializer loads a gamestatetree from a file
 */
@Slf4j
public final class Deserializer {

    /**
     * Deserializes the machine from a gfobject file
     *
     * @param filename the name of the file where the statemachine is stored
     * @return returns the object loaded from the gfobject file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <O> O load(String filename, Class<O> className) {
        try {
            FileInputStream file = new FileInputStream(filename + ((filename.contains(".gfobject")) ? "" : ".gfobject"));
            ObjectInputStream in = new ObjectInputStream(file);
            Object object = in.readObject();
            in.close();
            file.close();

            log.info("Object deserialized");

            return className.cast(object);
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
        return null;
    }
}
