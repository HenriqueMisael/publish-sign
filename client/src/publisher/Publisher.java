package publisher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            boolean active = true;
            do {
                System.out.println("Digite o assunto do evento");
                String subject = consoleReader.readLine();
                output.writeUTF("EVT:" + subject);

                System.out.println("Escolha uma ação:");
                System.out.println("[1] Digitar novo evento");
                System.out.println("[2] Sair");

                Integer option = null;
                do {
                    try {
                        String readed = consoleReader.readLine();
                        option = Integer.parseInt(readed);
                    } catch (NumberFormatException exception) {
                        System.err.println("Digite um valor inteiro correspondente à opção desejada");
                    }
                } while (option == null);

                switch (option) {
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
