package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ServerNode extends Client implements Publisher, Subscriber {

    private final Set<String> subscriptions;
    private final Map<String, Boolean> subscriptionsUpdates;
    private final Set<Event> events;

    public ServerNode(Socket socket) throws IOException {
        super(socket);
        subscriptionsUpdates = new HashMap<>();
        subscriptions = new HashSet<>();
        events = new HashSet<>();
    }

    private boolean hasNewMessage() {
        try {
            int available = inputStream.available();
            return available > 0;
        } catch (IOException e) {
            System.out.println("Erro:" + e.getMessage());
            return false;
        }
    }

    private String getMessage() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Set<Event> checkForEvents() {

        checkForNewInputs();
        Set<Event> copy = new HashSet<>(events);
        events.clear();

        return copy;
    }

    @Override
    public Stream<Map.Entry<String, Boolean>> checkSubscriptionsUpdate() {
        checkForNewInputs();
        Set<Map.Entry<String, Boolean>> copy = new HashSet<>(subscriptionsUpdates.entrySet());
        subscriptionsUpdates.clear();
        return copy.stream();
    }

    private void checkForNewInputs() {
        while (hasNewMessage()) {
            String[] request = getMessage().split(":");

            String subject = request[1];
            switch (request[0]) {
                case "SUB":
                    subscriptionsUpdates.put(subject, true);
                    break;
                case "UNSUB":
                    subscriptionsUpdates.put(subject, false);
                    break;
                case "EVT":
                    events.add(new Event(subject));
                    break;
            }
        }
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(Event event) {
        this.send(event.toString());
    }

    @Override
    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    public void updateSubscription(Map.Entry<String, Boolean> entry) {
        StringBuilder update = new StringBuilder();
        if (entry.getValue()) {
            update.append("SUB");
        } else {
            update.append("UNSUB");
        }
        update.append(":").append(entry.getKey());
        send(update.toString());
    }
}
