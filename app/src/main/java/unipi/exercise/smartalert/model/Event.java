package unipi.exercise.smartalert.model;

public class Event {
    private EventType eventType;
    private String municipality;

    public Event(EventType eventType, String municipality) {
        this.eventType = eventType;
        this.municipality = municipality;

    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
