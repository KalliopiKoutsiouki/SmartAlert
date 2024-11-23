package unipi.exercise.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unipi.exercise.smartalert.adapter.EventAdapter;
import unipi.exercise.smartalert.helper.AtticaMunicipalities;
import unipi.exercise.smartalert.httpclient.RetrofitClient;
import unipi.exercise.smartalert.model.Event;
import unipi.exercise.smartalert.model.UserData;

public class CivilProtectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private List<UserData> userList;
    private RelativeLayout RL_Logout;
    private ImageButton refreshList;
    private ImageButton metricsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        RL_Logout = findViewById(R.id.RL_Logout);
        RL_Logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshList = findViewById(R.id.btn_refresh_list);
        refreshList.setOnClickListener(v -> {
            loadEventData();
        });
        // Initialize data
        eventList = new ArrayList<>();
        loadEventData();
//        loadUserData();

        // Set up adapter with the event list
        eventAdapter = new EventAdapter(eventList, userList, this);
        recyclerView.setAdapter(eventAdapter);
        metricsButton = findViewById(R.id.btn_metrics);
        metricsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CivilProtectionActivity.this, MetricsActivity.class);
            startActivity(intent);
        });
    }


    private void loadEventData() {

        RetrofitClient.getApiService().getImportantEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList = response.body();
                    boolean isLanguageGreek = Locale.getDefault().getLanguage().equals("el");
                    for (Event event : eventList) {
                        if (isLanguageGreek) {
                            // Translate event type
                            switch (event.getEventType()) {
                                case "Earthquake":
                                    event.setEventType(getString(R.string.earthquake));
                                    break;
                                case "Fire":
                                    event.setEventType(getString(R.string.fire));
                                    break;
                                case "Flood":
                                    event.setEventType(getString(R.string.flood));
                                    break;
                                case "Tornado":
                                    event.setEventType(getString(R.string.tornado));
                                    break;
                            }

                            // Translate municipalities
                            List<String> translatedMunicipalities = new ArrayList<>();
                            for (String municipality : event.getMunicipalities()) {
                                translatedMunicipalities.add(AtticaMunicipalities.translateToGreek(municipality));
                            }
                            event.setMunicipalities(translatedMunicipalities);
                        }
                    }

                    // Set the adapter with the updated event list
                    eventAdapter = new EventAdapter(eventList, userList, CivilProtectionActivity.this);
                    recyclerView.setAdapter(eventAdapter);
                } else {
                    Toast.makeText(CivilProtectionActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching events", t);
                Toast.makeText(CivilProtectionActivity.this, R.string.error_fetching_events, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
