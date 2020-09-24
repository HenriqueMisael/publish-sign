package publisher;

import util.ConsoleUI;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.util.Arrays.asList;

public class Publisher {

    public static void main(String name, Socket socket) {
        Publisher publisher = new Publisher(name, socket);
        publisher.start();
    }

    private final String name;
    private final Socket socket;
    private Long nextEventID;

    public Publisher(String name, Socket socket) {
        this.name = name;
        this.socket = socket;

        this.nextEventID = 1L;
    }

    private void start() {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("IAM:PUBLISHER:" + this.name);

            boolean active = true;
            do {
                String subject = ConsoleUI.inputString("Digite o assunto do evento");
                output.writeUTF("EVT:" + this.name + this.nextEventID++ + ":" + subject);

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
