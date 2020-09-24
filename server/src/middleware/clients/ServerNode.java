package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerNode extends Client implements Publisher, Subscriber {

    private final Set<String> subscriptions;
    private final Map<String, Boolean> subscriptionsUpdates;
    private final Set<Event> newEvents;
    private final Set<String> oldEventIDs;

    public ServerNode(Socket socket) throws IOException {
        super(socket);
        subscriptionsUpdates = new HashMap<>();
        subscriptions = new HashSet<>();
        newEvents = new HashSet<>();
        oldEventIDs = new HashSet<>();
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
        Set<Event> copy = new HashSet<>(newEvents);
        newEvents.clear();

        this.oldEventIDs.addAll(copy.stream().map(event -> event.id).collect(Collectors.toSet()));

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

            switch (request[0]) {
                case "SUB":
                    subscriptionsUpdates.put(request[1], true);
                    break;
                case "UNSUB":
                    subscriptionsUpdates.put(request[1], false);
                    break;
                case "EVT":
                    String id = (request[1]);
                    if (!this.oldEventIDs.contains(id)) {
                        String subject = request[2];
                        newEvents.add(new Event(id, subject));
                    }
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
