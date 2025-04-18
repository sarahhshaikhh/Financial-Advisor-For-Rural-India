package com.example.navdhan;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, dobEditText, mobileNumberEditText, passwordEditText, confirmPasswordEditText;
    private Button verifyAccountButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language from SharedPreferences
        String languageCode = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getString("SelectedLanguage", "en"); // Default to English

        // Set the locale based on the saved language
        updateLocale(languageCode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users"); // Reference to "users" table

        // Initialize UI elements
        nameEditText = findViewById(R.id.name);
        dobEditText = findViewById(R.id.dob); // Added for DOB
        mobileNumberEditText = findViewById(R.id.mobile_number);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        verifyAccountButton = findViewById(R.id.btn_verify_account);
        loginTextView = findViewById(R.id.loginac_text);

        // Verify Account Button Click
        verifyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String dob = dobEditText.getText().toString().trim(); // Get DOB
                String mobileNumber = mobileNumberEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (!name.isEmpty() && !dob.isEmpty() && !mobileNumber.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        checkIfMobileExists(name, dob, mobileNumber, password);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login Text Click Listener
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class); // Intent to navigate to LoginActivity
                startActivity(intent);
            }
        });
    }

    private void updateLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();

        // Update locale for API levels 17 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        // Apply configuration changes
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private void checkIfMobileExists(String name, String dob, String mobileNumber, String password) {
        mDatabase.orderByChild("mobile").equalTo(mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mobile number already exists in the database
                    Toast.makeText(SignUpActivity.this, "Mobile number already registered. Please log in.", Toast.LENGTH_SHORT).show();
                } else {
                    // Mobile number does not exist; proceed with registration
                    saveUserToDatabase(name, dob, mobileNumber, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Error checking mobile number: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(String name, String dob, String mobileNumber, String password) {
        // Save user details to Firebase Database
        String userId = mDatabase.push().getKey();
        if (userId != null) {
            User user = new User(name, dob, mobileNumber, password);
            mDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    redirectToOtpActivity(name, dob, mobileNumber, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void redirectToOtpActivity(String name, String dob, String mobileNumber, String password) {
        // Save user data temporarily in a bundle or Firebase database for later use
        Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("dob", dob);
        intent.putExtra("mobile", mobileNumber);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    // User class to represent user details
    public static class User {
        public String name, dob, mobile, password;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String name, String dob, String mobile, String password) {
            this.name = name;
            this.dob = dob;
            this.mobile = mobile;
            this.password = password;
        }
    }
}
