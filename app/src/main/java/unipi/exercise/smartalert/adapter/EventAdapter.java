package unipi.exercise.smartalert.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import unipi.exercise.smartalert.R;
import unipi.exercise.smartalert.helper.AtticaMunicipalities;
import unipi.exercise.smartalert.httpclient.RetrofitClient;
import unipi.exercise.smartalert.model.Event;
import unipi.exercise.smartalert.model.EventNotification;
import unipi.exercise.smartalert.model.UserData;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter class for managing and displaying a list of events in a RecyclerView.
 * This adapter handles event notifications and user interactions such as alerting and dismissing events.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private List<Event> eventList;
    private List<UserData> userList;
    private Context context;
    private FirebaseFirestore db;

    /**
     * Constructor for the EventAdapter.
     *
     * @param eventList List of events to display.
     * @param userList List of users to process notifications.
     * @param context Context of the activity or fragment using this adapter.
     */
    public EventAdapter(List<Event> eventList, List<UserData> userList, Context context) {
        this.eventList = eventList;
        this.userList = userList;
        this.context = context;
    }

    /**
     * Creates and inflates the view holder for an event item.
     *
     * @param parent The parent view group.
     * @param viewType The view type of the new view.
     * @return A new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        db = FirebaseFirestore.getInstance();
        loadUserData();
        return new EventViewHolder(view);
    }

    /**
     * Binds the data for a specific event to the view holder.
     *
     * @param holder The EventViewHolder instance.
     * @param position The position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        setEventIcon(holder.eventIcon, event.getEventType());

        holder.eventTypeText.setText(event.getEventType());
        holder.locationText.setText(String.join(", ", event.getMunicipalities()));

        // Handle button clicks
        holder.alertButton.setOnClickListener(v -> {
            loadUserData();
            List<UserData> filteredUsers = filterUsersByLocation(event);
            if (!filteredUsers.isEmpty()) {
                sendNotificationRequest(filteredUsers, event);
            } else {
                Toast.makeText(context, R.string.no_users_match_this_location, Toast.LENGTH_SHORT).show();
            }
        });

        holder.dismissButton.setOnClickListener(v -> {
            Call<Void> call = RetrofitClient.getApiService().rejectEvent(event.getEventId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(
                                context,
                                context.getString(R.string.alert_rejected_for) + " " + event.getEventType(),
                                Toast.LENGTH_SHORT
                        ).show();
                        eventList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, R.string.failed_to_reject_alert, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Sets the icon and color for an event based on its type.
     *
     * @param eventIcon The ImageView where the icon will be displayed.
     * @param eventType The type of the event (e.g., Earthquake, Flood, etc.).
     */
    private void setEventIcon(ImageView eventIcon, String eventType) {
        int iconResId = 0;
        int iconColor = 0;
        String earthquake = context.getString(R.string.earthquake);
        String flood = context.getString(R.string.flood);
        String fire = context.getString(R.string.fire);
        String tornado = context.getString(R.string.tornado);

        if (eventType.equals(earthquake)) {
            iconResId = R.drawable.ic_earthquake_icon;
            iconColor = context.getResources().getColor(R.color.earthquake, context.getTheme());
        } else if (eventType.equals(flood)) {
            iconResId = R.drawable.ic_flood_icon;
            iconColor = context.getResources().getColor(R.color.flood, context.getTheme());
        } else if (eventType.equals(fire)) {
            iconResId = R.drawable.ic_fire_icon;
            iconColor = context.getResources().getColor(R.color.fire, context.getTheme());
        } else if (eventType.equals(tornado)) {
            iconResId = R.drawable.ic_tornado_icon;
            iconColor = context.getResources().getColor(R.color.tornado, context.getTheme());
        }

        eventIcon.setImageResource(iconResId);
        eventIcon.setColorFilter(iconColor);
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The number of events.
     */
    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    /**
     * Filters the user list based on their location and the event's affected municipalities.
     *
     * @param event The event to filter users by.
     * @return A list of users matching the event's location.
     */
    private List<UserData> filterUsersByLocation(Event event) {
        List<UserData> filteredUsers = new ArrayList<>();
        Map<String, GeoPoint> municipalitiesMap = AtticaMunicipalities.getMunicipalitiesMap();
        boolean isLanguageGreek = Locale.getDefault().getLanguage().equals("el");
        if (isLanguageGreek) {
            List<String> translatedMunicipalities = new ArrayList<>();
            for (String municipality : event.getMunicipalities()) {
                translatedMunicipalities.add(AtticaMunicipalities.translateToEnglish(municipality));
            }
            event.setMunicipalities(translatedMunicipalities);
        }

        for (UserData user : userList) {
            GeoPoint userLocation = user.getHomeLocation();

            // Iterate over event municipalities to check if the user's location matches any of them
            for (String municipality : event.getMunicipalities()) {
                if (municipalitiesMap.containsKey(municipality)) {
                    GeoPoint eventMunicipalityLocation = municipalitiesMap.get(municipality);

                    // Compare the user's location to the municipality's location
                    if (userLocation.getLatitude() == eventMunicipalityLocation.getLatitude() &&
                            userLocation.getLongitude() == eventMunicipalityLocation.getLongitude()) {
                        filteredUsers.add(user);
                    }
                }
            }
        }
        return filteredUsers;
    }

    /**
     * Sends a notification request to the backend for the specified event and users.
     *
     * @param users The list of users to notify.
     * @param event The event triggering the notification.
     */
    private void sendNotificationRequest(List<UserData> users, Event event) {
        List<String> userTokens = new ArrayList<>();
        for (UserData user : users) {
            userTokens.add(user.getDeviceToken());
        }

        EventNotification eventNotification = new EventNotification(event, userTokens);
        Call<Void> call = RetrofitClient.getApiService().sendNotification(eventNotification);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            context,
                            context.getString(R.string.alert_sent_for) + " " + event.getEventType(),
                            Toast.LENGTH_SHORT
                    ).show();
                    Map<String, Object> pushedEventData  = new HashMap<>();
                    pushedEventData.put("event_id", event.getEventId());
                    if(Locale.getDefault().getLanguage().equals("el")){
                        pushedEventData.put("event_type", translateEventType(event.getEventType()));
                    } else {
                        pushedEventData.put("event_type", event.getEventType());
                    }
                    pushedEventData.put("timestamp", LocalDate.now());

                    db.collection("pushed_events").document()
                            .set(pushedEventData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Success", "Pushed event with id: " + event.getEventId() + " added to db.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Failure", "Failed to insert event with id: " + event.getEventId() + " to db.");

                                }
                            });
                } else {
                    Toast.makeText(context, R.string.failed_to_send_alert, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String translateEventType(String eventType) {
        switch (eventType) {
            case "Σεισμός":
                eventType = "Earthquake";
                break;
            case "Φωτιά":
                eventType = "Fire";
                break;
            case "Πλημμύρα":
                eventType = "Flood";
                break;
            case "Ανεμοστρόβιλος":
                eventType = "Tornado";
                break;
        }
        return eventType;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTypeText, locationText;
        Button alertButton, dismissButton;
        ImageView eventIcon; // Add this line


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeText = itemView.findViewById(R.id.event_type_text);
            locationText = itemView.findViewById(R.id.location_text);
            alertButton = itemView.findViewById(R.id.alert_button);
            dismissButton = itemView.findViewById(R.id.dismiss_button);
            eventIcon = itemView.findViewById(R.id.event_icon); // Initialize the ImageView

        }
    }
    /**
     * Loads user data from Firestore and populates the user list.
     */
        private void loadUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize Firestore
        CollectionReference usersCollection = db.collection("users"); // Assuming you store users under the "users" collection

        if (userList == null) {
            userList = new ArrayList<>();
        }

        // Get the users from the Firestore collection
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    // Clear the existing user list
                    userList.clear();

                    // Loop through the documents in the Firestore collection
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String email = document.getString("email");
                        GeoPoint homeLocation = document.getGeoPoint("coordinates");
                        String deviceToken = document.getString("device_token");
                        String role = document.getString("role");

                        // Create a new UserData object and add it to the list
                        if (email != null && homeLocation != null && deviceToken != null) {
                            UserData user = new UserData(homeLocation, email, role, deviceToken);
                            userList.add(user);
                        }
                    }
                }
            } else {
                // Handle failure
                Log.e("Firestore", "Error getting users", task.getException());
                Toast.makeText(context, R.string.error_fetching_users, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
