import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {

        // 1. Setup - non-blocking server channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(6379));
        serverChannel.configureBlocking(false);  // KEY: don't block on accept()

        // 2. Create the selector - this IS the event loop
        Selector selector = Selector.open();

        // 3. Register server channel for ACCEPT events
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port 6379");

        // 4. THE EVENT LOOP
        while (true) {
            selector.select();  // blocks until at least one event is ready

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();  // IMPORTANT: manually remove processed key

                if (key.isAcceptable()) {
                    handleAccept(serverChannel, selector);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    // Called when a new client wants to connect
    private static void handleAccept(ServerSocketChannel serverChannel, Selector selector)
            throws IOException {

        SocketChannel clientChannel = serverChannel.accept(); // won't block - we know it's ready
        clientChannel.configureBlocking(false);               // client also non-blocking

        // Register this client for READ events
        clientChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    // Called when a client has sent data
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            // Client disconnected
            System.out.println("Client disconnected");
            clientChannel.close();
            key.cancel();
            return;
        }

        // For now, respond PONG to everything
        // Later you'll parse the buffer to read actual commands
        clientChannel.write(ByteBuffer.wrap("+PONG\r\n".getBytes()));
    }
}