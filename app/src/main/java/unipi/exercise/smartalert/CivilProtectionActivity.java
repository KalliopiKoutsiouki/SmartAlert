package unipi.exercise.smartalert;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import unipi.exercise.smartalert.adapter.EventAdapter;
import unipi.exercise.smartalert.model.Event;
import unipi.exercise.smartalert.model.EventType;

public class CivilProtectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        eventList = new ArrayList<>();
        loadEventData();

        // Set up adapter with the event list
        eventAdapter = new EventAdapter(eventList, this);
        recyclerView.setAdapter(eventAdapter);
    }


    private void loadEventData() {

        eventList.add(new Event(EventType.FLOOD, "Downtown Area"));
        eventList.add(new Event(EventType.EARTHQUAKE, "Suburban Area"));
        eventList.add(new Event(EventType.FIRE, "Industrial Park"));
        eventList.add(new Event(EventType.TORNADO, "Coastal Area"));
    }
}
