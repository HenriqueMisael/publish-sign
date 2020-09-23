package middleware.clients;

import middleware.Event;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface Subscriber {

    Stream<Map.Entry<String, Boolean>> checkSubscriptionsUpdate();

    void send(Event event);

    Set<String> getSubscriptions();

    void close() throws IOException;
}
