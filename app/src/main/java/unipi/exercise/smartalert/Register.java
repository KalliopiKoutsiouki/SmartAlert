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

import java.util.HashMap;
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.municipalities_array, R.layout.spinner_item_layout);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
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
                Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Register.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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

                            // Store user data in Firestore with role
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("role", role);
                            userData.put("coordinates", userCoordinates);

                            db.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Success","User " + email +  " with role: " + role + " added to db.");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Failure", "Failed to insert user " + email +  " with role: " + role + ",to db.");
                                            // rollback for firebase authentication
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> deleteTask) {
                                                            if (deleteTask.isSuccessful()) {
                                                                Log.d("rollback firebase","Could not save user role, rollback authentication registration");
                                                            } else {
                                                                Log.e("rollback failed", "Even your rollback failed, you loser!");
                                                            }
                                                        }
                                                    });
                                        }
                                    });

                            Toast.makeText(Register.this, "Account created successfully.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Account already exists.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
