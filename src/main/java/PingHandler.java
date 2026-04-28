public class PingHandler implements CommandHandler{
    @Override
    public String handle(String[] args) {
        return RespEncoder.simpleString("PONG");
    }
}
