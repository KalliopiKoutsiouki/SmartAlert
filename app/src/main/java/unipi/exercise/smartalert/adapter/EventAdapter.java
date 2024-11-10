package unipi.exercise.smartalert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import unipi.exercise.smartalert.R;
import unipi.exercise.smartalert.model.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventTypeText.setText(event.getEventType().getValue());
        holder.locationText.setText(event.getMunicipality());

        // Handle button clicks
        holder.alertButton.setOnClickListener(v ->
                Toast.makeText(context, "Alert for " + event.getEventType(), Toast.LENGTH_SHORT).show()
        );
        holder.dismissButton.setOnClickListener(v ->
                Toast.makeText(context, "Dismissed " + event.getEventType(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTypeText, locationText;
        Button alertButton, dismissButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeText = itemView.findViewById(R.id.event_type_text);
            locationText = itemView.findViewById(R.id.location_text);
            alertButton = itemView.findViewById(R.id.alert_button);
            dismissButton = itemView.findViewById(R.id.dismiss_button);
        }
    }
}
