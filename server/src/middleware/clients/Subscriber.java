package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Subscriber extends Client {

    public final Set<String> subscriptions;

    public Subscriber(Socket socket) throws IOException {
        super(socket);
        subscriptions = new HashSet<>();
    }

    public void checkSubscriptionsUpdate() {
        try {
            while (inputStream.available() > 0) {
                String[] request = inputStream.readUTF().split(":");

                String subject = request[1];
                switch (request[0]) {
                    case "SUB":
                        System.out.println("New subscription: " + subject);
                        subscriptions.add(subject);
                        break;
                    case "UNSUB":
                        System.out.println("Unsubscription: " + subject);
                        subscriptions.remove(subject);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Event event) {
        try {
            outputStream.writeUTF(event.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
