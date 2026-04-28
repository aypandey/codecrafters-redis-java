import java.util.HashMap;
import java.util.Map;

public class CommandRouter {
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public CommandRouter() {
        // Register all handlers here — adding new commands is just one line
        handlers.put("PING", new PingHandler());
        handlers.put("ECHO", new EchoHandler());
    }

    public String route(String[] args) {
        if (args == null || args.length == 0) {
            return RespEncoder.error("empty command");
        }

        String command = args[0].toUpperCase();  // Redis commands are case-insensitive
        CommandHandler handler = handlers.get(command);

        if (handler == null) {
            return RespEncoder.error("unknown command '" + command + "'");
        }

        return handler.handle(args);
    }
}
