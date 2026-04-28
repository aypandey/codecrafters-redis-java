public class EchoHandler implements CommandHandler{
    @Override
    public String handle(String[] args) {
        if (args.length < 2) {
            return RespEncoder.error("wrong number of arguments for 'echo' command");
        }
        return RespEncoder.bulkString(args[1]);
    }
}
