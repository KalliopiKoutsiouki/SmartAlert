package unipi.exercise.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Activity for handling user login functionality.
 * This activity authenticates users using Firebase Authentication, redirects them to appropriate activities
 * based on their role, and provides an option to register a new account.
 */
public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is already logged in and redirect to MainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        // Initialize UI elements
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        // Redirect to Register activity
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        // Login button click listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }
    /**
     * Handles the login process by validating user input, authenticating with Firebase,
     * and redirecting the user to the appropriate activity based on their role.
     */
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        // Validate email input
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.email_is_required));
            editTextEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Validate password input
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.password_is_required));
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Authenticate the user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String userEmail = mAuth.getCurrentUser().getEmail();

                            // Use whereEqualTo to query the email field
                            db.collection("users")
                                    .whereEqualTo("email", userEmail)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {
                                                boolean roleFound = false;

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String role = document.getString("role");

                                                    // Redirect to CivilProtectionActivity for civil-protection role
                                                    if ("civil-protection".equals(role)) {
                                                        roleFound = true;
                                                        Toast.makeText(Login.this, R.string.welcome_civil_protection, Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), CivilProtectionActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    }
                                                }
                                                // Redirect to MainActivity for non-civil-protection users
                                                if (!roleFound) {
                                                    Toast.makeText(Login.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            } else {

                                                Toast.makeText(Login.this, R.string.user_role_not_found, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Login.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
