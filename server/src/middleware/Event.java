package middleware;

public class Event {

    public final String subject;

    public static Event fromMessage(String message) {

        String subject = message.split(":")[1];

        return new Event(subject);
    }

    public Event(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "EVT:" + subject;
    }
}
