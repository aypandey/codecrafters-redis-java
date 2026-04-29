// RedisStore.java
import java.util.concurrent.ConcurrentHashMap;

public class RedisStore {

    // Singleton — one store for the whole server
    private static final RedisStore INSTANCE = new RedisStore();
    public static RedisStore getInstance() { return INSTANCE; }

    private final ConcurrentHashMap<String, RedisValue> store = new ConcurrentHashMap<>();

    private RedisStore() {}

    // O(1) average
    public void set(String key, String value) {
        store.put(key, RedisValue.of(value));
    }

    // SET with expiry (PX = milliseconds, EX = seconds)
    public void set(String key, String value, long expiresAt) {
        store.put(key, RedisValue.of(value, expiresAt));
    }

    // O(1) average — returns null if missing or expired
    public String get(String key) {
        RedisValue val = store.get(key);

        if (val == null) return null;

        // Lazy expiry — check on access, like Redis does
        if (val.isExpired()) {
            store.remove(key);   // clean up expired key
            return null;
        }

        return val.getValue();
    }

    public boolean delete(String key) {
        return store.remove(key) != null;
    }

    public boolean exists(String key) {
        return get(key) != null;  // reuses expiry check in get()
    }
}