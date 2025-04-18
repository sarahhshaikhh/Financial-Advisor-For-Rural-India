package com.example.navdhan;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {

    private static final String TAG = "LanguageSelectionActivity";
    private HashMap<String, String> languageCodes;
    private boolean isFirstSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        initializeLanguageCodes();

        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        String[] languages = getResources().getStringArray(R.array.language_names);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, languages);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                String selectedLanguage = (String) parent.getItemAtPosition(position);
                String languageCode = languageCodes.get(selectedLanguage);
                if (languageCode != null) {
                    setLocale(languageCode);
                } else {
                    Toast.makeText(LanguageSelectionActivity.this, "Language code not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void initializeLanguageCodes() {
        languageCodes = new HashMap<>();
        languageCodes.put("English", "en");
        languageCodes.put("Hindi", "hi");
        languageCodes.put("Gujarati", "gu");
        languageCodes.put("Tamil", "ta");
        languageCodes.put("Bengali", "bn");
        languageCodes.put("Telugu", "te");
        languageCodes.put("Marathi", "mr");
        languageCodes.put("Punjabi", "pa");
        languageCodes.put("Malayalam", "ml");
        languageCodes.put("Odia", "or");
        languageCodes.put("Kannada", "kn");
        languageCodes.put("Assamese", "as");
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save the selected language to SharedPreferences
        getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .edit()
                .putString("SelectedLanguage", languageCode)
                .apply();

        // Redirect to the login page
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}