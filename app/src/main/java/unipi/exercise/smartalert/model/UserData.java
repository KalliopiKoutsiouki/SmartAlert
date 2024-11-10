package unipi.exercise.smartalert.model;

import com.google.firebase.firestore.GeoPoint;

public class UserData {

    private GeoPoint homeLocation;
    private String email;
    private Role role;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
