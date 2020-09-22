package middleware;

import middleware.clients.ClientType;
import middleware.clients.Publisher;
import middleware.clients.Subscriber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Middleware {

    private static final int CONNECTIONS_LIMIT = Integer.MAX_VALUE;
    private static final int TIME_FOR_EACH_LOOP = 10000;
    private final ServerSocket serverSocket;
    private final Set<Publisher> publishers;
    private final Set<Subscriber> subscribers;
    private final Set<Socket> otherServers;

    public Middleware(ServerSocket serverSocket, Set<Socket> otherServers) {
        this.serverSocket = serverSocket;
        this.otherServers = otherServers;

        publishers = new HashSet<>();
        subscribers = new HashSet<>();

        otherServers.forEach(socket -> {
            try {
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("IAM:MIDDLEWARE");
                subscribers.add(new Subscriber(socket));
                publishers.add(new Publisher(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() throws InterruptedException {
        new Thread(this::checkForNewConnections).start();

        System.out.println("Waiting for publishers");
        while (publishers.isEmpty()) {
            Thread.sleep(1000);
        }
        while (!publishers.isEmpty()) {
            checkForSubscriptionsUpdate();

            checkForEvents();
            System.out.println("Waiting " + TIME_FOR_EACH_LOOP / 1000 + " seconds for next checking");
            Thread.sleep(TIME_FOR_EACH_LOOP);
            System.out.println();

            // Remove conexões inativas com os publicadores
            publishers.removeAll(publishers.stream().filter(publisher -> !publisher.isActive()).collect(Collectors.toSet()));
            // Remove conexões inativas com os subscritores
            subscribers.removeAll(subscribers.stream().filter(publisher -> !publisher.isActive()).collect(Collectors.toSet()));
        }
        close();
    }

    private void checkForSubscriptionsUpdate() {

        List<String> subscriptions = subscribers.stream().flatMap(subscriber -> subscriber.subscriptions.stream()).collect(Collectors.toList());

        Set<Map.Entry<String, Boolean>> subscriptionsUpdate = subscribers.stream().flatMap(Subscriber::checkSubscriptionsUpdate).filter(update -> !subscriptions.contains(update.getKey())).collect(Collectors.toSet());

        subscriptionsUpdate.forEach(entry -> {

            StringBuilder update = new StringBuilder();
            if (entry.getValue()) {
                update.append("SUB");
            } else {
                update.append("UNSUB");
            }
            update.append(":").append(entry.getKey());

            this.otherServers.forEach(socket -> {
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    output.writeUTF(update.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void close() {
        System.out.println("Closing...");
        subscribers.forEach(subscriber -> {
            try {
                subscriber.close();
            } catch (IOException e) {
                System.err.println("Could not close subscriber correctly");
            }
        });
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkForEvents() {
        System.out.println("Checking for new events");

        Set<Event> events = publishers.stream()
                .flatMap(publisher -> publisher.checkForEvents().stream())
                .collect(Collectors.toSet());

        if (!events.isEmpty()) {
            events.forEach(this::sendEvent);
        }

        System.out.println("Events checkout finished");
    }

    private void sendEvent(Event event) {

        System.out.println("Processing event " + event.subject);

        Set<Subscriber> subscribers = this.subscribers.stream()
                .filter(subscriber -> subscriber.subscriptions.contains(event.subject))
                .collect(Collectors.toSet());

        subscribers.forEach(subscriber -> subscriber.send(event));
    }

    private void checkForNewConnections() {
        do {
            try {
                Socket socketClient = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(socketClient.getInputStream());
                String iam = inputStream.readUTF();
                String[] iamArray = iam.split(":");
                ClientType clientType = ClientType.valueOf(iamArray[1]);

                System.out.println("Found client " + clientType.toString());

                switch (clientType) {
                    case SUBSCRIBER:
                        subscribers.add(new Subscriber(socketClient));
                        break;

                    case PUBLISHER:
                        publishers.add(new Publisher(socketClient));
                        break;
                    case MIDDLEWARE:
                        publishers.add(new Publisher(socketClient));
                        subscribers.add(new Subscriber(socketClient));
                        otherServers.add(socketClient);
                        break;
                }
            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (publishers.size() + subscribers.size() < CONNECTIONS_LIMIT);
    }
}
