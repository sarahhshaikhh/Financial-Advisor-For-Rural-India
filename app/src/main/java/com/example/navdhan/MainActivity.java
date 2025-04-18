package com.example.navdhan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String CHATBOT_ID = "eHpV-ddzQ1vQSs-9fb-yK"; // Replace with your Chatbot ID
    private final String API_KEY = "b0af51d0-01eb-4acf-9321-ebbf0c60152f"; // Replace with your API key
    private final String API_URL = "https://www.chatbase.co/api/v1/chat"; // Set your API URL
    private List<Message> conversationMessages = new ArrayList<>();
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> chatMessages = new ArrayList<>();
    private EditText messageInput;
    private View sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView, Adapter, and UI elements
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Set up RecyclerView and Adapter
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);

        // Set up OnClickListener for the send button
        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage); // Send the message
                messageInput.setText(""); // Clear input field after sending
            } else {
                Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String userMessage) {
        // Add user message to the list
        conversationMessages.add(new Message(userMessage, "user"));
        chatMessages.add(new Message(userMessage, "user"));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

        // Start a new thread to get the bot's response
        new Thread(() -> {
            String botResponse = getBotResponse(userMessage);

            runOnUiThread(() -> {
                if (botResponse != null) {
                    conversationMessages.add(new Message(botResponse, "assistant"));
                    chatMessages.add(new Message(botResponse, "assistant"));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Error retrieving response", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String getBotResponse(String userMessage) {
        String response = null;
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            JSONArray messagesArray = new JSONArray();

            for (Message message : conversationMessages) {
                JSONObject messageObject = new JSONObject();
                messageObject.put("content", message.getContent());
                messageObject.put("role", message.getRole());
                messagesArray.put(messageObject);
            }

            requestBody.put("messages", messagesArray);
            requestBody.put("chatbotId", CHATBOT_ID);

            // Print the request body for debugging
            System.out.println("Request Body: " + requestBody.toString());

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.toString().getBytes());
            outputStream.close();


            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                String apiResponse = responseBuilder.toString();

                // Log the API response for debugging
                System.out.println("API Response: " + apiResponse);

                response = parseBotResponse(apiResponse);
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorBuilder = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorBuilder.append(line);
                }
                System.err.println("Error Response: " + errorBuilder.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    private String parseBotResponse(String apiResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(apiResponse);

            return jsonResponse.optString("text", "Error: Response text not found");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error: Could not parse response";
        }
    }


}
