package subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Subscriber {

    public static void main(Socket socket) {
        Subscriber subscriber = new Subscriber(socket);
        subscriber.start();
    }

    private DataInputStream input;
    private DataOutputStream output;

    public Subscriber(Socket socket) {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            output.writeUTF("I am a subscriber");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
