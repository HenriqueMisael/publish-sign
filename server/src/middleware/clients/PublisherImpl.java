package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class PublisherImpl extends Client implements Publisher {

    private final String name;

    public PublisherImpl(Socket socket, String name) throws IOException {
        super(socket);
        this.name = name;
    }

    private boolean hasNewEvent() {
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

        Set<Event> events = new HashSet<>();

        while (hasNewEvent()) {
            String message = getMessage();
            events.add(Event.fromMessage(message));
        }

        return events;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
