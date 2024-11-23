package unipi.exercise.smartalert.model;

public class PushedEvent {

    private String eventId;
    private String eventType;
    private String timestamp;

    public PushedEvent() {
    }

    public PushedEvent(String eventId, String eventType, String timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
