package unipi.exercise.smartalert;

import static unipi.exercise.smartalert.helper.AtticaMunicipalities.getMunicipalitiesMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unipi.exercise.smartalert.helper.AtticaMunicipalities;
import unipi.exercise.smartalert.model.Role;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonReg;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private FirebaseFirestore db;
    private Spinner municipalitySpinner;
    private Map<String, GeoPoint> municipalitiesMap = getMunicipalitiesMap(); // Load map of municipalities

    private static final String role = Role.CONCERNED_CITIZEN.getValue();

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        municipalitySpinner = findViewById(R.id.municipality_spinner);

        List<String> municipalities = new ArrayList<>();
        if (AtticaMunicipalities.isSystemLanguageGreek()) {
            // Add Greek translations of the municipalities
            for (String municipality : AtticaMunicipalities.getMunicipalitiesMap().keySet()) {
                municipalities.add(AtticaMunicipalities.translateToGreek(municipality));
            }
        } else {
            // Use the original municipality names
            municipalities.addAll(AtticaMunicipalities.getMunicipalitiesMap().keySet());
        }

        // Create the adapter with the dynamically generated list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, municipalities);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);

        // Set the adapter to the spinner
        municipalitySpinner.setAdapter(adapter);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        db = FirebaseFirestore.getInstance();

        // OnClickListener to redirect to the Login activity
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Register button logic
        buttonReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());
            String confirmPassword = String.valueOf(editTextConfirmPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, R.string.please_enter_your_email, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, R.string.please_enter_your_password, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Register.this, R.string.please_confirm_your_password, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Register.this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create user with email and password in Firebase
            createUser(email, password);
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String selectedMunicipality = ((Spinner) findViewById(R.id.municipality_spinner))
                                    .getSelectedItem().toString();
                            GeoPoint userCoordinates = getMunicipalitiesMap().get(selectedMunicipality);
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            // Retrieve the FCM token for this user
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(tokenTask -> {
                                        if (tokenTask.isSuccessful()) {
                                            String token = tokenTask.getResult();
                                            Log.d("FCM Token", "Retrieved FCM Token: " + token);

                                            // Store user data in Firestore with role, coordinates, and token
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("email", email);
                                            userData.put("role", role);
                                            userData.put("coordinates", userCoordinates);
                                            userData.put("device_token", token);  // Save the FCM token

                                            // Save user data to Firestore
                                            db.collection("users").document(uid)
                                                    .set(userData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Success", "User " + email + " with role: " + role + " and token added to db.");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("Failure", "Failed to insert user " + email + " with role: " + role + " to db.");
                                                            // Rollback Firebase authentication if saving to Firestore fails
                                                            user.delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> deleteTask) {
                                                                            if (deleteTask.isSuccessful()) {
                                                                                Log.d("Rollback", "Could not save user role, rollback authentication registration");
                                                                            } else {
                                                                                Log.e("Rollback Failed", "Even your rollback failed!");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        } else {
                                            // Handle token retrieval failure
                                            Log.e("FCM Token Error", "Failed to retrieve FCM token");
                                        }
                                    });

                            Toast.makeText(Register.this, R.string.account_created_successfully, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Register.this, R.string.account_already_exists, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
