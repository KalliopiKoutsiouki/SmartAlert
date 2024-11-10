package unipi.exercise.smartalert.model;

public enum Role {


    CONCERNED_CITIZEN ("concerned-citizen"),
    CIVIL_PROTECTION("civil-protection");
    private final String value;
    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
