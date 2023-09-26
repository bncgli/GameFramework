package it.game.framework.serializations;

import it.game.framework.exceptions.GameException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * Serializes an object in a gfobject file
 */
@Slf4j
public final class Serializer {

    /**
     * Write the target object in the file with the supplied filename
     * @param target The object to be saved
     * @param filename the name of the file where will be stored
     * @throws IOException
     */
    public static void save(Object target, String filename) {
        try {
            FileOutputStream file = new FileOutputStream(filename+ ((filename.contains(".gfobject")) ? "" : ".gfobject"));
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(target);
            out.close();
            file.close();

            log.info("Object deserialized");
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
    }

}
