package it.game.framework.contexts;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Callables;
import it.game.framework.exceptions.ExceptionLibrary;
import it.game.framework.exceptions.GameException;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class GuavaContext extends GameContext {

    Cache<String, Object> cache;

    public GuavaContext() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build();
    }

    @Override
    public boolean contains(String key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public <V> void put(String key, V value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(Map<String, Object> data) {
        cache.putAll(data);
    }

    @Override
    public <V> V get(String key, Class<V> clazz) throws Exception {
        var r = cache.getIfPresent(key);
        if(!clazz.isInstance(r)) throw new GameException(ExceptionLibrary.get("CONTEXT_CASTING_EXCEPTION"));
        return clazz.cast(r);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key) throws GameException {
        return (V) cache.getIfPresent(key);
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

    @Override
    public void cleanup() {
        cache.cleanUp();
    }

}
