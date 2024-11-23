package unipi.exercise.smartalert;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MetricsActivity extends AppCompatActivity {

    int filtersCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        Toolbar toolbar = findViewById(R.id.metricsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.home);
        loadMetricsData();
        EdgeToEdge.enable(this);
    }

    private void loadMetricsData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pushed_events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Map<String, Integer>> eventData = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventType = document.getString("event_type");

                            HashMap<String, Object> localDate = (HashMap<String, Object>) document.get("timestamp");

                            String month = (String) localDate.get("month");
                            String year = String.valueOf(localDate.get("year"));

                            String monthYear = year + "-" + month;


                            eventData.putIfAbsent(monthYear, new HashMap<>());
                            Map<String, Integer> monthData = eventData.get(monthYear);
                            monthData.put(eventType, monthData.getOrDefault(eventType, 0) + 1);
                        }
                        setupFilters(eventData);
                        // Pass the eventData to the chart drawing function
                        drawGroupedBarChart(eventData);
                    } else {
                        Log.e("Firestore Error", "Error getting documents", task.getException());
                    }
                });
    }

    private void setupFilters(Map<String, Map<String, Integer>> eventData) {
        CheckBox filterFire = findViewById(R.id.filterFire);
        CheckBox filterFlood = findViewById(R.id.filterFlood);
        CheckBox filterEarthquake = findViewById(R.id.filterEarthquake);
        CheckBox filterTornado = findViewById(R.id.filterTornado);

        TextView filtersErrorMessage = findViewById(R.id.filtersErrorMessage);
        BarChart barChart = findViewById(R.id.groupedBarChart);


        // Listener to redraw chart when filter changes
        View.OnClickListener filterListener = view -> {
            countFiltersSelected(filterFire, filterFlood, filterEarthquake, filterTornado);
            if (filtersCounter > 0) {
                filtersErrorMessage.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                drawGroupedBarChart(eventData);
            } else {
                filtersErrorMessage.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
            }
        };

        filterFire.setOnClickListener(filterListener);
        filterFlood.setOnClickListener(filterListener);
        filterEarthquake.setOnClickListener(filterListener);
        filterTornado.setOnClickListener(filterListener);
    }

    private void countFiltersSelected(CheckBox... checkBoxes) {
        filtersCounter = 0;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                filtersCounter++;
            }
        }
    }

    private void drawGroupedBarChart(Map<String, Map<String, Integer>> eventData) {
        BarChart barChart = findViewById(R.id.groupedBarChart);

        CheckBox filterFire = findViewById(R.id.filterFire);
        CheckBox filterFlood = findViewById(R.id.filterFlood);
        CheckBox filterEarthquake = findViewById(R.id.filterEarthquake);
        CheckBox filterTornado = findViewById(R.id.filterTornado);

        // Prepare entries for each event type
        List<BarEntry> fireEntries = new ArrayList<>();
        List<BarEntry> floodEntries = new ArrayList<>();
        List<BarEntry> earthquakeEntries = new ArrayList<>();
        List<BarEntry> tornadoEntries = new ArrayList<>();

        List<String> monthYearList  = new ArrayList<>(eventData.keySet());
        sortMonthsAndYears(monthYearList);

        for (int i = 0; i < monthYearList.size(); i++) {
            String monthYear = monthYearList.get(i);
            Map<String, Integer> monthData = eventData.get(monthYear);

            if (filterFire.isChecked())
                fireEntries.add(new BarEntry(i, monthData.getOrDefault("Fire", 0)));
            if (filterFlood.isChecked())
                floodEntries.add(new BarEntry(i, monthData.getOrDefault("Flood", 0)));
            if (filterEarthquake.isChecked())
                earthquakeEntries.add(new BarEntry(i, monthData.getOrDefault("Earthquake", 0)));
            if (filterTornado.isChecked())
                tornadoEntries.add(new BarEntry(i, monthData.getOrDefault("Tornado", 0)));
        }

        List<IBarDataSet> dataSets = new ArrayList<>();
        if (!fireEntries.isEmpty()) dataSets.add(new BarDataSet(fireEntries, getString(R.string.fire)));
        if (!floodEntries.isEmpty()) dataSets.add(new BarDataSet(floodEntries, getString(R.string.flood)));
        if (!earthquakeEntries.isEmpty()) dataSets.add(new BarDataSet(earthquakeEntries, getString(R.string.earthquake)));
        if (!tornadoEntries.isEmpty()) dataSets.add(new BarDataSet(tornadoEntries, getString(R.string.tornado)));

        if (dataSets.size() > 0) {
            BarData data = new BarData(dataSets);
            // Assign colors
            ((BarDataSet) dataSets.get(0)).setColor(0xFF56B7F1); // Blue
            if (dataSets.size() > 1) ((BarDataSet) dataSets.get(1)).setColor(0xFFCDA67F); // Brown
            if (dataSets.size() > 2) ((BarDataSet) dataSets.get(2)).setColor(0xFFFEA47F); // Orange
            if (dataSets.size() > 3) ((BarDataSet) dataSets.get(3)).setColor(0xFF9B59B6); // Purple

            float groupSpace = 0.3f; // Space between groups (months)
            float barSpace = 0.05f;  // Space between bars within a group


            // Group bars
            barChart.setData(data);
            if(filtersCounter>1){
                barChart.groupBars(0f, groupSpace, barSpace);
            }
            float barWidth = (1f - groupSpace) / (float) filtersCounter - barSpace; // For 4 bar sets
            data.setBarWidth(barWidth); // Set bar width

            barChart.enableScroll();

            barChart.setFitBars(true);

            // Set X-Axis labels
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f); // Set granularity to 1 (each group represents one month)
            xAxis.setCenterAxisLabels(true); // Center labels between groups
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setAxisMinimum(0f);
            boolean isGreek = Locale.getDefault().getLanguage().equals("el");

            List<String> translatedMonthYearList = new ArrayList<>();
            for (String monthYear : monthYearList) {
                String[] parts = monthYear.split("-");  // Split into [year, month]
                String year = parts[0];  // Year part remains unchanged
                String month = parts[1];  // Month part will be translated

                // Translate month if system language is Greek
                String translatedMonth = isGreek ? translateToGreek(month) : month;
                translatedMonthYearList.add(year + "-" + translatedMonth);
            }
            xAxis.setValueFormatter(new IndexAxisValueFormatter(translatedMonthYearList)); // Set month labels

            barChart.getDescription().setEnabled(false);
            // Refresh chart
            barChart.invalidate();
        } else {
            Log.e("Chart Error", "Cannot group bars with fewer than 2 datasets.");
            barChart.invalidate();
        }
    }

    private String translateToGreek(String month) {
        switch (month.toUpperCase()) {
            case "JANUARY": return "ΙΑΝΟΥΑΡΙΟΣ";
            case "FEBRUARY": return "ΦΕΒΡΟΥΑΡΙΟΣ";
            case "MARCH": return "ΜΑΡΤΙΟΣ";
            case "APRIL": return "ΑΠΡΙΛΙΟΣ";
            case "MAY": return "ΜΑΙΟΣ";
            case "JUNE": return "ΙΟΥΝΙΟΣ";
            case "JULY": return "ΙΟΥΛΙΟΣ";
            case "AUGUST": return "ΑΥΓΟΥΣΤΟΣ";
            case "SEPTEMBER": return "ΣΕΠΤΕΜΒΡΙΟΣ";
            case "OCTOBER": return "ΟΚΤΩΒΡΙΟΣ";
            case "NOVEMBER": return "ΝΟΕΜΒΡΙΟΣ";
            case "DECEMBER": return "ΔΕΚΕΜΒΡΙΟΣ";
            default: return month; // In case of an unexpected month name
        }
    }

    public static void sortMonthsAndYears(List<String> monthYearList) {
        List<String> chronologicalOrder = Arrays.asList(
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        );

        // Custom comparator: Compare by year first, then by month
        monthYearList.sort((entry1, entry2) -> {
            // Split the year and month from "YEAR-MONTH" format
            String[] parts1 = entry1.split("-");
            String[] parts2 = entry2.split("-");

            int year1 = Integer.parseInt(parts1[0]);
            int year2 = Integer.parseInt(parts2[0]);

            String month1 = parts1[1];
            String month2 = parts2[1];

            // Compare by year first
            if (year1 != year2) {
                return Integer.compare(year1, year2);
            }

            // If years are equal, compare by month order
            return Integer.compare(chronologicalOrder.indexOf(month1), chronologicalOrder.indexOf(month2));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}