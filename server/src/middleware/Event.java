package middleware;

public class Event {

    public final String id;
    public final String subject;

    public static Event fromMessage(String message) {

        String[] splitMessage = message.split(":");
        String id = (splitMessage[1]);
        String subject = splitMessage[2];

        return new Event(id, subject);
    }

    public Event(String id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "EVT:" + this.id + ":" + subject;
    }
}
