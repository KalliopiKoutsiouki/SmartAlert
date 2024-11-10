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

//    private void loginUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//        progressBar.setVisibility(View.VISIBLE);
//
//        if (TextUtils.isEmpty(email)) {
//            editTextEmail.setError("Email is required.");
//            editTextEmail.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            return;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            editTextPassword.setError("Password is required.");
//            editTextPassword.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            return;
//        }
//
//        // Authenticate the user
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
//                        if (task.isSuccessful()) {
//                            FirebaseFirestore db = FirebaseFirestore.getInstance();
//                            String userEmail = mAuth.getCurrentUser().getEmail();
//                            db.collection("users").document(userEmail).get()
//                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            if (task.isSuccessful() && task.getResult() != null) {
//
//                                                DocumentSnapshot document = task.getResult();
//                                                String role = document.getString("role");
//
//                                                if ("civil-protection".equals(role)) {
//                                                    Toast.makeText(Login.this, "Welcome, Civil Protection", Toast.LENGTH_SHORT).show();
//                                                    Intent intent = new Intent(getApplicationContext(), CivilProtectionActivity.class);
//                                                    startActivity(intent);
//                                                } else {
//                                                    // Redirect to MainActivity for other roles
//                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                                    startActivity(intent);
//                                                }
//                                                finish();
//                                            } else {
//                                                // Error fetching document or role does not exist
//                                                Toast.makeText(Login.this, "User role not found.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Authenticate the user
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

                                                    if ("civil-protection".equals(role)) {
                                                        roleFound = true;
                                                        Toast.makeText(Login.this, "Welcome, Civil Protection", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), CivilProtectionActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    }
                                                }

                                                if (!roleFound) {

                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                finish();
                                            } else {

                                                Toast.makeText(Login.this, "User role not found.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
