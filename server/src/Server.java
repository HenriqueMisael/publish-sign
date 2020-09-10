import middleware.Middleware;

import java.io.IOException;
import java.net.ServerSocket;

import static java.lang.Integer.parseInt;

public class Server {

    public static void main(String... args) {
        int port = parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Middleware middleware = new Middleware(serverSocket);
            middleware.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
