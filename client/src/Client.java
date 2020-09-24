import publisher.Publisher;
import subscriber.Subscriber;

import java.io.IOException;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class Client {

    public static void main(String... args) {
        ClientType clientType = ClientType.valueOf(args[0]);

        String name = args[1];
        String serverAddress = args[2];
        int serverPort = parseInt(args[3]);

        try (Socket socket = new Socket(serverAddress, serverPort)) {

            System.out.println("Connected");
            switch (clientType) {
                case PUBLISHER:
                    Publisher.main(name, socket);
                    break;
                case SUBSCRIBER:
                    Subscriber.main(name, socket);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
