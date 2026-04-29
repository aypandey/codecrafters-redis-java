// RedisValue.java
public class RedisValue {

    // Mirrors Redis's internal encodings
    public enum Encoding {
        INT,      // value is a long   e.g. "42"
        EMBSTR,   // short string ≤ 44 chars
        RAW       // long string > 44 chars
    }

    private final Object data;          // either Long or String
    private final Encoding encoding;
    private final long expiresAt;       // epoch ms, -1 = no expiry

    private static final int EMBSTR_THRESHOLD = 44;

    private RedisValue(Object data, Encoding encoding, long expiresAt) {
        this.data = data;
        this.encoding = encoding;
        this.expiresAt = expiresAt;
    }

    // Factory — picks the best encoding automatically
    public static RedisValue of(String value, long expiresAt) {
        // Try INT encoding first — most compact
        try {
            long num = Long.parseLong(value);
            return new RedisValue(num, Encoding.INT, expiresAt);
        } catch (NumberFormatException ignored) {}

        // Pick EMBSTR vs RAW based on length
        Encoding enc = value.length() <= EMBSTR_THRESHOLD
                ? Encoding.EMBSTR
                : Encoding.RAW;

        return new RedisValue(value, enc, expiresAt);
    }

    public static RedisValue of(String value) {
        return of(value, -1);  // no expiry
    }

    // Always returns as String (for GET responses)
    public String getValue() {
        return data.toString();
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() > expiresAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}