package publisher;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static javax.swing.JOptionPane.*;

public class Publisher {

    public static void main(Socket socket) {
        Publisher publisher = new Publisher(socket);
        publisher.start();
    }

    private final Socket socket;

    public Publisher(Socket socket) {
        this.socket = socket;
    }

    private void start() {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("IAM:PUBLISHER");

            boolean active = true;
            do {
                String subject = JOptionPane.showInputDialog("Digite o assunto do evento");
                output.writeUTF("EVT:" + subject);

                int option = JOptionPane.showConfirmDialog(null, "Deseja enviar outro evento?");
                switch (option) {
                    case YES_OPTION:
                        continue;
                    case CLOSED_OPTION:
                    case NO_OPTION:
                    default:
                        active = false;
                }
            } while (active);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
