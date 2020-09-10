package subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.String.join;

public class Subscriber {

    public static void main(Socket socket, Iterable<String> subjects) {
        Subscriber subscriber = new Subscriber(socket);
        subscriber.start(subjects);
    }

    private final Socket socket;

    public Subscriber(Socket socket) {
        this.socket = socket;
    }

    private void start(Iterable<String> subjects) {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String subjectsString = join(",", subjects);
            output.writeUTF("IAM:SUBSCRIBER:" + subjectsString);

            DataInputStream input = new DataInputStream(socket.getInputStream());

            while (socket.isConnected()) {
                String message = input.readUTF();

                System.out.println("Event arrived: " + message.split(":")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
