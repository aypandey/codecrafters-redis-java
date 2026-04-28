public class RespParser {
    public static String[] parse(String raw) {
        String[] parts = raw.split("\r\n");

        // parts[0] = "*2"  → array of 2 elements
        // parts[1] = "$4"  → next string is 4 bytes
        // parts[2] = "ECHO"
        // parts[3] = "$3"  → next string is 3 bytes
        // parts[4] = "hey"

        int argCount = Integer.parseInt(parts[0].substring(1)); // strip "*"
        String[] args = new String[argCount];

        // Every even index (1,3,5...) is a length prefix ($N)
        // Every odd index (2,4,6...) is the actual value
        int argIndex = 0;
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].startsWith("$")) continue;  // skip length prefix
            args[argIndex++] = parts[i];
            if (argIndex == argCount) break;
        }

        return args; // args[0] = command, args[1..] = arguments
    }
}
