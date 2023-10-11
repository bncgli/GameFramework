package it.game.framework.contexts;

import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.exceptions.GameException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleContext implements GameContext {

    List<String> names;
    List<Object> objects;

    public SimpleContext() {
        this.names = new ArrayList<>();
        this.objects = new ArrayList<>();
    }

    private int indexOf(String key) {
        return names.indexOf(key);
    }

    @Override
    public <V> void put(String key, V value) {
        if (names.contains(key)) {
            objects.set(indexOf(key), value);
        } else {
            names.add(key);
            objects.add(value);
        }
    }

    @Override
    public <V> void putAll(Map<String, V> data) {
        for (Map.Entry<String, ?> k : data.entrySet()) {
            put(k.getKey(), k.getValue());
        }
    }

    @Override
    public <V> V get(String key, Class<V> clazz) throws GameException {
        Object o = objects.get(indexOf(key));
        if (!clazz.isInstance(o))
            throw new GameException(ExceptionLibrary.get("CONTEXT_CASTING_EXCEPTION"), " key: " + key + "(" + o.toString() + " -> " + clazz.getSimpleName() + ")");
        return clazz.cast(o);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key) throws GameException {
        return (V) objects.get(indexOf(key));
    }

    @Override
    public void remove(String key) {
        int i = indexOf(key);
        names.remove(i);
        objects.remove(i);
    }

    @Override
    public void cleanup() {
        names.clear();
        objects.clear();
    }
}
