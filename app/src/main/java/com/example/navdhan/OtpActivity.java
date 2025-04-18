package com.example.navdhan;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private EditText otpEditText;
    private Button requestOtpButton, enterOtpButton;
    private String mobileNumber, userName, password;
    private String verificationId;

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
        setContentView(R.layout.activity_otp);

        // Retrieve user data passed from SignUpActivity
        userName = getIntent().getStringExtra("name");
        mobileNumber = getIntent().getStringExtra("mobile");
        password = getIntent().getStringExtra("password");

        // Validate the phone number
        if (!mobileNumber.startsWith("+91")) {
            mobileNumber = "+91" + mobileNumber; // Add country code if missing
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        otpEditText = findViewById(R.id.otp);
        requestOtpButton = findViewById(R.id.btn_request);
        enterOtpButton = findViewById(R.id.btn_enter_otp);

        // Request OTP when the button is clicked
        requestOtpButton.setOnClickListener(v -> sendOtp());

        // Verify OTP when the button is clicked
        enterOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (!otp.isEmpty()) {
                verifyOtp(otp);
            } else {
                Toast.makeText(OtpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber, // Ensure proper format
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            otpEditText.setText(code);
                            verifyOtp(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        OtpActivity.this.verificationId = verificationId;
                        Toast.makeText(OtpActivity.this, "OTP Sent to " + mobileNumber, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        storeUserData();
                        Toast.makeText(OtpActivity.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to Login Activity
                        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OtpActivity.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
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
    private void storeUserData() {
        String userId = mDatabase.push().getKey();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("name").setValue(userName);
            mDatabase.child("users").child(userId).child("mobile").setValue(mobileNumber);
            mDatabase.child("users").child(userId).child("password").setValue(password); // Ensure secure password storage
        }
    }
}
