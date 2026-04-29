public class SetHandler implements CommandHandler {

    private final RedisStore store = RedisStore.getInstance();

    @Override
    public String handle(String[] args) {
        // Minimum: SET key value
        if (args.length < 3) {
            return RespEncoder.error("wrong number of arguments for 'set' command");
        }

        String key   = args[1];
        String value = args[2];

        // Parse optional EX / PX flags
        // SET key value EX 10   → expire in 10 seconds
        // SET key value PX 5000 → expire in 5000 milliseconds
        if (args.length >= 5) {
            String flag = args[3].toUpperCase();
            try {
                long ttl = Long.parseLong(args[4]);
                long expiresAt = switch (flag) {
                    case "EX" -> System.currentTimeMillis() + (ttl * 1000);
                    case "PX" -> System.currentTimeMillis() + ttl;
                    default   -> throw new IllegalArgumentException("unknown flag");
                };
                store.set(key, value, expiresAt);
            } catch (Exception e) {
                return RespEncoder.error("invalid expire time in 'set' command");
            }
        } else {
            store.set(key, value);
        }

        return RespEncoder.simpleString("OK");
    }
}