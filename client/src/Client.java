import publisher.Publisher;
import subscriber.Subscriber;

import java.io.IOException;
import java.net.Socket;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class Client {

    public static void main(String... args) {
        ClientType clientType = ClientType.valueOf(args[0]);

        String serverAddress = args[1];
        int serverPort = parseInt(args[2]);

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            switch (clientType) {
                case PUBLISHER:
                    Publisher.main(socket);
                    break;
                case SUBSCRIBER:
                    Subscriber.main(socket, asList(args[3].split(",")));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
