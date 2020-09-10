package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.net.Socket;

public class Publisher extends Client {

    public Publisher(Socket socket) throws IOException {
        super(socket);
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

    public Event checkForEvent() {
        if (!hasNewEvent())
            return null;

        String message = getMessage();

        return Event.fromMessage(message);
    }
}
