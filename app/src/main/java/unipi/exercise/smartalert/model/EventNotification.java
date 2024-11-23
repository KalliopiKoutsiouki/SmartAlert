package unipi.exercise.smartalert.model;

import java.util.List;

public class EventNotification {

    private Event event;
    private List<String> userTokens;

    public EventNotification(Event event, List<String> userTokens) {
        this.event = event;
        this.userTokens = userTokens;
    }

    public Event getEventId() {
        return event;
    }

    public void setEventId(Event eventId) {
        this.event = event;
    }

    public List<String> getUserTokens() {
        return userTokens;
    }

    public void setUserTokens(List<String> userTokens) {
        this.userTokens = userTokens;
    }
}
