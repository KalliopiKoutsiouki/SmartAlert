package unipi.exercise.smartalert.model;

import com.google.firebase.firestore.GeoPoint;

public class UserData {

    private GeoPoint homeLocation;
    private String email;
    private String role;
    private String deviceToken;

    public UserData(GeoPoint homeLocation, String email, String role, String deviceToken) {
        this.homeLocation = homeLocation;
        this.email = email;
        this.role = role;
        this.deviceToken = deviceToken;
    }

    public GeoPoint getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(GeoPoint homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
