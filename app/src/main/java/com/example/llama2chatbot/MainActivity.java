package com.example.llama2chatbot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        Button goButton = findViewById(R.id.goButton);

        goButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            if (!username.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a username.", Toast.LENGTH_LONG).show();
            }
        });
    }
}

