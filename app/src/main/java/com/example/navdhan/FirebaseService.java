package com.example.navdhan;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseService {

    public static void initializeFirebase() {
        try {
            // Provide the path to your Service Account Key JSON file
            FileInputStream serviceAccount = new FileInputStream("Downloads/navdhan1-firebase-adminsdk-da7af-9b90a4f4a2.json");

            // Initialize Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()

                    .setDatabaseUrl("https://navdhan1-default-rtdb.firebaseio.com/") // Replace with your Firebase Database URL
                    .build();

            System.out.println("Firebase Initialized Successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Initialize Firebase
        initializeFirebase();
    }
}