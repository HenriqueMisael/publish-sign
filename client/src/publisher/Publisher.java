package publisher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Publisher {

    public static void main(Socket socket) {
        Publisher publisher = new Publisher(socket);
        publisher.start();
    }

    private DataInputStream input;
    private DataOutputStream output;

    public Publisher(Socket socket) {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            output.writeUTF("I am a publisher");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
