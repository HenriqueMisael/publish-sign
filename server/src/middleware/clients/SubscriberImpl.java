package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SubscriberImpl extends Client implements Subscriber {

    private final Set<String> subscriptions;
    private final String name;

    @Override
    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    public SubscriberImpl(Socket socket, String name) throws IOException {
        super(socket);
        this.name = name;
        subscriptions = new HashSet<>();
    }

    @Override
    public Stream<Map.Entry<String, Boolean>> checkSubscriptionsUpdate() {
        Map<String, Boolean> subscriptionsUpdates = new HashMap<>();
        try {
            while (inputStream.available() > 0) {
                String[] request = inputStream.readUTF().split(":");

                String subject = request[1];
                switch (request[0]) {
                    case "SUB":
                        System.out.println("New subscription: " + subject);
                        subscriptions.add(subject);
                        subscriptionsUpdates.put(subject, true);
                        break;
                    case "UNSUB":
                        System.out.println("Unsubscription: " + subject);
                        subscriptions.remove(subject);
                        subscriptionsUpdates.put(subject, false);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subscriptionsUpdates.entrySet().stream();
    }

    @Override
    public void send(Event event) {
        try {
            outputStream.writeUTF(event.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
