package publisher;

import util.ConsoleUI;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.util.Arrays.asList;

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
                String subject = ConsoleUI.inputString("Digite o assunto do evento");
                output.writeUTF("EVT:" + subject);

                switch (ConsoleUI.menu("Escolha uma ação", asList("Digitar novo evento", "Sair"))) {
                    case 1:
                        continue;
                    case 2:
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
