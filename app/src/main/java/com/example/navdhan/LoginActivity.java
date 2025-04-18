package com.example.navdhan;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mobileNumberEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language from SharedPreferences
        String languageCode = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getString("SelectedLanguage", "en"); // Default to English

        // Set the locale based on the saved language
        updateLocale(languageCode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Initialize UI elements
        mobileNumberEditText = findViewById(R.id.mobile_number);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        signUpTextView = findViewById(R.id.signup_text);

        // Login Button Click Listener
        loginButton.setOnClickListener(v -> {
            String mobileNumber = mobileNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!TextUtils.isEmpty(mobileNumber) && !TextUtils.isEmpty(password)) {
                loginUser(mobileNumber, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Sign-Up Text Click Listener
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Method to update the app's locale
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

    // Method to authenticate user with Firebase
    private void loginUser(String mobileNumber, String password) {
        mDatabase.orderByChild("mobile").equalTo(mobileNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean loginSuccessful = false;

                            // Check if the password matches for the user
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String storedPassword = userSnapshot.child("password").getValue(String.class);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    loginSuccessful = true;
                                    break;
                                }
                            }

                            if (loginSuccessful) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                redirectToHome();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to redirect user to the home page
    private void redirectToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // Ensure HomeActivity exists
        startActivity(intent);
        finish();
    }
}
