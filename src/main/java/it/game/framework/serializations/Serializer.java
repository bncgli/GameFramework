package it.game.framework.serializations;

import it.game.framework.exceptions.GameException;
import it.game.framework.statemachines.StateMachineData;
import lombok.extern.slf4j.Slf4j;

import java.io.*;


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

    /**
     * Deserializes the machine from a gfobject file
     *
     * @param filename the name of the file where the statemachine is stored
     * @return returns the object loaded from the gfobject file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static StateMachineData load(String filename) {
        try {
            FileInputStream file = new FileInputStream(filename + ((filename.contains(".gfobject")) ? "" : ".gfobject"));
            ObjectInputStream in = new ObjectInputStream(file);
            Object object = in.readObject();
            in.close();
            file.close();

            log.info("Object deserialized");

            return (StateMachineData) object;
        } catch (Exception e) {
            log.error(GameException.format(e));
        }
        return null;
    }

}
