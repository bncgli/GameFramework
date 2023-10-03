package it.game.framework.contexts;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Callables;
import it.game.framework.exceptions.GameException;
import it.game.framework.exceptions.GameExceptionsLibrary;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class GuavaContext  implements GameContext{

    Cache<String, Object> cache;

    public GuavaContext() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build();
    }

    @Override
    public <V> void put(String key, V value) {
        cache.put(key, value);
    }

    @Override
    public <V> void putAll(Map<String, V> data) {
        cache.putAll(data);
    }

    @Override
    public <V> V get(String key, Class<V> clazz) throws Exception {
        var r = cache.getIfPresent(key);
        if(!clazz.isInstance(r)) throw new GameException(GameExceptionsLibrary.CONTEXT_CASTING_EXCEPTION);
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
