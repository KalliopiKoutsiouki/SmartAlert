package unipi.exercise.smartalert;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unipi.exercise.smartalert.httpclient.RetrofitClient;
import unipi.exercise.smartalert.model.EventReport;
import unipi.exercise.smartalert.model.EventType;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_CODE = 6849;
    private static final int CAMERA_CODE = 1111;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri imageUri;
    private RelativeLayout RL_Logout;
    private Button btnFire, btnFlood, btnTornado, btnEarthquake;
    private FusedLocationProviderClient fusedLocationClient;
    private static Location userEventLocation;
    private EventReport currentEventReport;
    private EventType eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RL_Logout = findViewById(R.id.RL_Logout);
        RL_Logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        btnFire = findViewById(R.id.btn_fire);
        btnFlood = findViewById(R.id.btn_flood);
        btnTornado = findViewById(R.id.btn_tornado);
        btnEarthquake = findViewById(R.id.btn_earthquake);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set onClickListeners for each button
        btnFire.setOnClickListener(v -> getLocationWithPermissionsThenProceed(EventType.FIRE));
        btnFlood.setOnClickListener(v -> getLocationWithPermissionsThenProceed(EventType.FLOOD));
        btnTornado.setOnClickListener(v -> getLocationWithPermissionsThenProceed(EventType.TORNADO));
        btnEarthquake.setOnClickListener(v -> getLocationWithPermissionsThenProceed(EventType.EARTHQUAKE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationFromFusedLocationClientAndCreateEvent();
            } else {
                Toast.makeText(this, "Location permission denied. Cannot proceed with event report.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserLocationFromFusedLocationClientAndCreateEvent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userEventLocation = location;
                        createEventReport(this.eventType);
                    } else {
                        Log.e("Location error", "Unable to get location.");
                        Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLocationWithPermissionsThenProceed(EventType eventType) {
        this.eventType = eventType;
        getUserLocationFromFusedLocationClientAndCreateEvent();
    }

    private void createEventReport(EventType eventType) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            if (userEventLocation != null) {
                EventReport eventReport = new EventReport();
                eventReport.setEventType(eventType.getValue());
                eventReport.setUserMail(userEmail);
                eventReport.setLatitude(userEventLocation.getLatitude());
                eventReport.setLongitude(userEventLocation.getLongitude());
                eventReport.setTimestamp(timestamp);

                new AlertDialog.Builder(this)
                        .setTitle("Attach Image")
                        .setMessage("Would you like to attach an image to this event report?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            currentEventReport = eventReport;
                            checkCameraPermission();
                        })
                        .setNegativeButton("No", (dialog, which) -> sendEventReport(eventReport))
                        .show();
            }
        } else {
            Log.e("Unauthorized User", "No user is currently logged in.");
            Toast.makeText(MainActivity.this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        } else {
            openCamera();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e("Camera Error", "Error occurred while creating the File", ex);
        }
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "unipi.exercise.smartalert.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentEventReport != null) {
                currentEventReport.setImageUri(imageUri.toString());
                sendEventReport(currentEventReport);
            }
        } else {
            sendEventReport(currentEventReport);
        }
    }

    private void sendEventReport(EventReport eventReport) {
        RetrofitClient.getApiService().sendEventReport(eventReport).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Event-Report", "Event Report Created: " + eventReport.toString());
                    Toast.makeText(MainActivity.this, "Event report sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to send event report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}