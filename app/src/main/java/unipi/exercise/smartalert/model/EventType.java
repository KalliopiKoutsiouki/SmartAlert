package unipi.exercise.smartalert.model;

public enum EventType {

    FLOOD ("Flood"),
    FIRE ("Fire"),
    EARTHQUAKE ("Earthquake"),
    TORNADO ("Tornado");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
