package com.example.llama2chatbot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends Activity {

    private ListView chatListView;
    private EditText messageEditText;
    private ChatMessageAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chatListView);
        messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);

        String username = getIntent().getStringExtra("username");

        chatHistory = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, chatHistory);
        chatListView.setAdapter(adapter);

        // Add initial bot welcome message
        addMessage("Welcome " + username + "!", false);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString();
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, true);
                sendChatMessage(userMessage);
                messageEditText.setText(""); // Clear the input box.
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        chatHistory.add(new ChatMessage(message, isUser));
        adapter.notifyDataSetChanged();
        chatListView.setSelection(chatHistory.size() - 1); // Scroll to the bottom.
    }

    private void sendChatMessage(String userMessage) {
        String url = "http://10.0.2.2:5000/chat";

        JSONObject chatObject = new JSONObject();
        try {
            chatObject.put("userMessage", userMessage);

            JSONArray chatHistoryJson = new JSONArray();
            for (ChatMessage message : chatHistory) {
                JSONObject messageJson = new JSONObject();
                if (message.isUser()) {
                    messageJson.put("User", message.getMessage());
                    messageJson.put("Llama", "");
                } else {
                    messageJson.put("Llama", message.getMessage());
                    messageJson.put("User", "");
                }
                chatHistoryJson.put(messageJson);
            }

            chatObject.put("chatHistory", chatHistoryJson);
            Log.d("ChatActivity", "Sending JSON: " + chatObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, chatObject,
                response -> {
                    try {
                        String botMessage = response.getString("message");
                        addMessage(botMessage, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addMessage("Failed to parse response.", false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("ChatActivity", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ChatActivity", "Status Code: " + error.networkResponse.statusCode);
                        Log.e("ChatActivity", "Response Data: " + new String(error.networkResponse.data));
                        addMessage("Error: " + new String(error.networkResponse.data), false);
                    } else {
                        addMessage("Error receiving response from the server.", false);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,  // 20 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}

