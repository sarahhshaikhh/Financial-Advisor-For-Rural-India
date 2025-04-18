package com.example.navdhan;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranslatorText {

    private static final String TAG = "TranslatorText";
    private static final String KEY = "A4X8MNEVnqfmUgDhku61oYVUtoZ9FiQEE3ZMD642smCZ47mmh4AsJQQJ99BAACGhslBXJ3w3AAAbACOGAjQf"; // Replace with your Azure Translator key
    private static final String ENDPOINT = "https://api.cognitive.microsofttranslator.com"; // Replace with your endpoint
    private static final String LOCATION = "centralindia"; // Replace with your Azure resource location
    private static final String API_VERSION = "3.0";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onTranslationCompleted(List<String> results);
        void onError(Exception e);
    }

    public void translateBatchTextAsync(List<String> texts, String toLanguageCode, Callback callback) {
        Log.d(TAG, "Starting translation...");
        executorService.execute(() -> {
            try {
                // Perform translation in the background
                List<String> translations = performTranslation(texts, toLanguageCode);

                // Pass result to the main thread
                mainHandler.post(() -> {
                    Log.d(TAG, "Translation completed successfully");
                    callback.onTranslationCompleted(translations);
                });

            } catch (Exception e) {
                // Pass error to the main thread
                mainHandler.post(() -> {
                    Log.e(TAG, "Translation failed", e);
                    callback.onError(e);
                });
            }
        });
    }

    private List<String> performTranslation(List<String> texts, String toLanguageCode) throws Exception {
        String route = "/translate?api-version=" + API_VERSION + "&to=" + toLanguageCode;
        String url = ENDPOINT + route;

        Log.d(TAG, "Request URL: " + url);

        // Build the request body
        JSONArray requestArray = new JSONArray();
        for (String text : texts) {
            JSONObject textObj = new JSONObject();
            textObj.put("Text", text);
            requestArray.put(textObj);
        }
        Log.d(TAG, "Request body: " + requestArray.toString());

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestArray.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", KEY)
                .addHeader("Ocp-Apim-Subscription-Region", LOCATION)
                .addHeader("Content-type", "application/json")
                .build();

        Log.d(TAG, "Sending request...");

        // Execute the request and parse the response
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.e(TAG, "Request failed with code: " + response.code());
            throw new IOException("Unexpected response code " + response.code() + ": " + response.body().string());
        }

        String responseBody = response.body().string();
        Log.d(TAG, "Response received: " + responseBody);

        JSONArray responseArray = new JSONArray(responseBody);
        List<String> translations = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject translationObject = responseArray.getJSONObject(i);
            JSONArray translationsArray = translationObject.getJSONArray("translations");
            if (translationsArray.length() > 0) {
                String translatedText = translationsArray.getJSONObject(0).getString("text");
                translations.add(translatedText);
            }
        }
        Log.d(TAG, "Parsed translations: " + translations);
        return translations;
    }
}
