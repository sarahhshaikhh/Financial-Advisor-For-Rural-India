package com.example.navdhan;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getContent());

        if ("user".equals(message.getRole())) {
            // User message styling
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble_background);
            holder.messageTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            ((LinearLayout.LayoutParams) holder.messageTextView.getLayoutParams()).gravity = Gravity.END;
        } else {
            // API response styling
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble_response);
            holder.messageTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
            ((LinearLayout.LayoutParams) holder.messageTextView.getLayoutParams()).gravity = Gravity.START;
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
