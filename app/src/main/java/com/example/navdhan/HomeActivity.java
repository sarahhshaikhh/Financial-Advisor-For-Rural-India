package com.example.navdhan;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;

import java.util.Locale;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View fabChatbot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language from SharedPreferences
        String languageCode = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getString("SelectedLanguage", "en"); // Default to English

        // Set the locale based on the saved language
        updateLocale(languageCode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize DrawerLayout, NavigationView, and FloatingActionButton (FAB)
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fabChatbot = findViewById(R.id.fab_chatbot);  // FloatingActionButton for chatbot

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the navigation item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Handle different drawer item clicks
            if (id == R.id.nav_settings) {
                // Handle home navigation logic here if needed
            } else if (id == R.id.nav_logout) {
                // Navigate to Settings Activity
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            } else if (id == R.id.terms_privacy) {
                // Navigate to Settings Activity
                startActivity(new Intent(HomeActivity.this, TermsActivity.class));
            }else if(id == R.id.fab_chatbot){
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }

            // Close the drawer after selection
            drawerLayout.closeDrawers();
            return true;
        });

        // Initialize the FloatingActionButton (FAB) for chatbot functionality
        fabChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent); // Open the chatbot screen
        });

        // ActionBarDrawerToggle to handle drawer icon state
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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
}
