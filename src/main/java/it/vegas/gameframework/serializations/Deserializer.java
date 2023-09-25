package it.vegas.gameframework.serializations;

import it.vegas.gameframework.exceptions.GameException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Slf4j
public final class Deserializer {

    public static <O> O load(String fileName, Class<O> className) {
        try {
            return className.cast(loadFromFile(fileName + ((fileName.contains(".gfobject")) ? "" : ".gfobject")));
        } catch (Exception ex) {
            log.error(GameException.format(ex));
        }
        return null;
    }

    private static Object loadFromFile(String filename) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(file);
        Object object = in.readObject();
        in.close();
        file.close();

        log.info("Object deserialized");

        return object;
    }
}
