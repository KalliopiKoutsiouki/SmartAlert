package unipi.exercise.smartalert.model;

import java.util.List;

public class Event {
    private Long eventId;
    private String eventType;
    private List<String> municipalities;

    public Event(Long eventId, String eventType, List<String> municipalities) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.municipalities = municipalities;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<String> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<String> municipalities) {
        this.municipalities = municipalities;
    }
}
