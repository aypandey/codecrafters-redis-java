public class RespEncoder {

    // Bulk string: "$3\r\nhey\r\n"
    public static String bulkString(String value) {
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }

    // Simple string: "+OK\r\n"
    public static String simpleString(String value) {
        return "+" + value + "\r\n";
    }

    // Error: "-ERR unknown command\r\n"
    public static String error(String message) {
        return "-ERR " + message + "\r\n";
    }

    // Integer: ":42\r\n"
    public static String integer(int value) {
        return ":" + value + "\r\n";
    }
}