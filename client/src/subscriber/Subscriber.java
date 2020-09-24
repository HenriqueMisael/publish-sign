package subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static util.ConsoleUI.*;

public class Subscriber {

    private final DataOutputStream output;
    private final List<String> subscribedSubjects;

    public static void main(String name, Socket socket) throws IOException {
        Subscriber subscriber = new Subscriber(name, socket);
        subscriber.start();
    }

    private final Socket socket;
    private final String name;

    public Subscriber(String name, Socket socket) throws IOException {
        this.name = name;
        this.socket = socket;
        output = new DataOutputStream(this.socket.getOutputStream());
        subscribedSubjects = new ArrayList<>();
    }

    private void start() {
        try {
            output.writeUTF("IAM:SUBSCRIBER:" + this.name);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            boolean active = true;
            while (active && socket.isConnected()) {

                breakLine();
                breakLine();

                if (subscribedSubjects.isEmpty()) {
                    subscribeOnSubject();
                }

                statement("Temas subscritos:");
                list(subscribedSubjects);
                breakLine();

                int option = menu("Escolha uma opção:", asList("Inscrever-se em outro tema", "Desinscrever-se de um tema", "Verificar novos eventos", "Sair"));
                switch (option) {
                    case 1:
                        subscribeOnSubject();
                        break;
                    case 2:
                        unsubscribeOnSubject();
                        break;
                    case 3:
                        while (input.available() > 0) {
                            String message = input.readUTF();
                            String[] response = message.split(":");
                            System.out.println("Event arrived: " + response[1] + "\t" + response[2]);
                        }
                        break;
                    default:
                        active = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribeOnSubject() {
        int option = menu("Tema a se desinscrever:", subscribedSubjects);
        String subject = subscribedSubjects.get(option - 1);
        try {
            output.writeUTF("UNSUB:" + subject);
            statement("Desinscrito com sucesso em " + subject);
            subscribedSubjects.remove(subject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeOnSubject() {
        String subject = inputString("Tema a se inscrever:");

        if (subject == null)
            return;

        try {
            output.writeUTF("SUB:" + subject);
            this.subscribedSubjects.add(subject);
            statement("Subscrito com sucesso em " + subject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
