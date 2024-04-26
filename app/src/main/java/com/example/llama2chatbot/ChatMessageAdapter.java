package com.example.llama2chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getTag() == null || !convertView.getTag().equals(message.isUser())) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    message.isUser() ? R.layout.user_message : R.layout.bot_message, parent, false);
            convertView.setTag(message.isUser()); // Set a tag to prevent style bleeding
        }

        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        messageTextView.setText(message.getMessage());

        return convertView;
    }
}

