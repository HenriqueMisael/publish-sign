package middleware.clients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public final Socket socket;
    protected final DataInputStream inputStream;
    protected final DataOutputStream outputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {
        socket.close();
    }

    public boolean isActive() {
        return !socket.isClosed() && socket.isConnected();
    }
}
