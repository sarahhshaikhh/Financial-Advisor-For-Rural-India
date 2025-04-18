plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // This is the correct syntax for Kotlin DSL
}

android {
    namespace = "com.example.navdhan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.navdhan"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase dependencies (do not include duplicates)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Ensure this is the BOM version

    // Firebase Realtime Database (optional if you need to store data)
    implementation("com.google.firebase:firebase-database") // No need for the specific version; Firebase BOM handles it

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (optional if you need Firestore)
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Messaging (optional if you're using FCM)
    implementation("com.google.firebase:firebase-messaging")

    // Volley for network requests (chatbot API calls)
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.ui.text.android)

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("androidx.appcompat:appcompat:1.4.0") // Example dependency for Android app
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")  // OkHttp for HTTP requests
    implementation ("com.google.code.gson:gson:2.9.0")      // Gson for JSON parsing

}