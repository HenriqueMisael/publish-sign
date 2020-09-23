package middleware.clients;

import middleware.Event;

import java.util.Set;

public interface Publisher {

    Set<Event> checkForEvents();
}
