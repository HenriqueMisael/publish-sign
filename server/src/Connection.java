import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {

    private DataInputStream input;
    private DataOutputStream output;

    public Connection(Socket socket) {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            String message = input.readUTF();
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
