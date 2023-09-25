package it.game.framework.serializations;

import it.game.framework.exceptions.GameException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
public final class Serializer {

    public static <O> void save(O target, String fileName) {
        try {
            writeOnFile(target, fileName + ((fileName.contains(".gfobject")) ? "" : ".gfobject"));
        } catch (Exception ex) {
            log.error(GameException.format(ex));
        }
    }

    private static void writeOnFile(Object target, String filename) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(target);
        out.close();
        file.close();

        log.info("Object deserialized");
    }

}
