package middleware;

import middleware.clients.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Middleware {

    private static final int CONNECTIONS_LIMIT = Integer.MAX_VALUE;
    private static final int TIME_FOR_EACH_LOOP = 20000;
    private final ServerSocket serverSocket;
    private final Set<Publisher> publishers;
    private final Set<Subscriber> subscribers;
    private final Set<ServerNode> otherServers;

    public Middleware(ServerSocket serverSocket, Set<Socket> otherServers) {
        this.serverSocket = serverSocket;
        this.publishers = new HashSet<>();
        this.subscribers = new HashSet<>();
        this.otherServers = otherServers
                .stream()
                .map(this::addNewServer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private ServerNode addNewServer(Socket socket) {
        ServerNode serverNode = null;

        try {
            serverNode = new ServerNode(socket);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("IAM:MIDDLEWARE");

            subscribers.add(serverNode);
            publishers.add(serverNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverNode;
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
        }
        close();
    }

    private void checkForSubscriptionsUpdate() {
        subscribers.forEach(subscriber -> subscriber.checkSubscriptionsUpdate().forEach(entry ->
                this.otherServers.stream().filter(serverNode -> serverNode.equals(subscriber)).forEach(serverNode -> {

                    serverNode.updateSubscription(entry);
                })
        ));
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

        publishers.forEach(publisher -> {
            Set<Event> events = publisher.checkForEvents();

            if (events.isEmpty())
                return;

            System.out.println("Events received: " + events.stream().map(event -> event.subject).collect(Collectors.joining(", ")));
            this.subscribers
                    .stream()
                    .filter(subscriber -> !publisher.equals(subscriber))
                    .forEach(subscriber -> events.forEach(event -> {
                        if (subscriber.getSubscriptions().contains(event.subject))
                            subscriber.send(event);
                    }));
        });

        System.out.println("Events checkout finished");
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
                        subscribers.add(new SubscriberImpl(socketClient));
                        break;

                    case PUBLISHER:
                        publishers.add(new PublisherImpl(socketClient));
                        break;
                    case MIDDLEWARE:
                        otherServers.add(addNewServer(socketClient));
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
