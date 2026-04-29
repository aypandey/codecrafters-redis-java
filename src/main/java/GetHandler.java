public class GetHandler implements CommandHandler {

    private final RedisStore store = RedisStore.getInstance();

    @Override
    public String handle(String[] args) {
        if (args.length < 2) {
            return RespEncoder.error("wrong number of arguments for 'get' command");
        }

        String value = store.get(args[1]);

        // Null bulk string "$-1\r\n" is Redis's way of saying "key not found"
        return value != null
                ? RespEncoder.bulkString(value)
                : RespEncoder.nullBulkString();
    }
}