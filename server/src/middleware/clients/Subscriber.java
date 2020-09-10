package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class Subscriber extends Client {

    public final Set<String> subscriptions;

    public Subscriber(Socket socket, Set<String> subscriptions) throws IOException {
        super(socket);
        this.subscriptions = subscriptions;
    }

    public void send(Event event) {
        try {
            outputStream.writeUTF(event.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
