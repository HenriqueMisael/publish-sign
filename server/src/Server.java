import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class Server {

    public static void main(String... args) {
        int port = parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socketClient = serverSocket.accept();

            Connection connection = new Connection(socketClient);
            connection.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
