import middleware.Middleware;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Server {

    public static void main(String... args) {
        int serverPort = parseInt(args[0]);
        Set<Socket> otherServers = args.length < 2 ? new HashSet() : Arrays.stream(args[1].split(",")).map(s -> s.split(":")).map((String[] addressPort) -> {
            String address = addressPort[0];
            int port = parseInt(addressPort[1]);

            try {
                return new Socket(address, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Middleware middleware = new Middleware(serverSocket, otherServers);
            middleware.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
