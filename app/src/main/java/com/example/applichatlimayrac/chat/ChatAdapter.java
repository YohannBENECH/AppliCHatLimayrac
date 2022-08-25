package com.example.applichatlimayrac.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applichatlimayrac.Globals;
import com.example.applichatlimayrac.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatList> chatLists;
    private final Context context;
    private String sUsername = "";
    private String sUsernameColor = "";
    String sUserNameLastMessage = "";

    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
        this.sUsername = Globals.LOGGED_USER_NAME;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {

        ChatList list2 = chatLists.get(position);

        if(list2 != null) {
            // If message's Username equals to currently logged Username
            if(list2.getsUsername().equals(sUsername)) {
                holder.myLayout.setVisibility(View.VISIBLE);
                holder.oppoLayout.setVisibility(View.GONE);

                holder.myMessage.setText(list2.getsMessage());
                holder.myDateTime.setText(list2.getsDateTime());

                // sUserNameLastMessage = list2.getsUsername();
            } else {
                holder.myLayout.setVisibility(View.GONE);
                holder.oppoLayout.setVisibility(View.VISIBLE);

                holder.oppoMessage.setText(list2.getsMessage());
                holder.oppoDateTime.setText(list2.getsDateTime());

                holder.oppoUsername.setText(list2.getsUsername());
                holder.oppoUsername.setTextColor(Color.parseColor(list2.getsUserColor()));

                // If the current message's Username is the same as the previous one
                /*
                if(!list2.getsUsername().equals(sUserNameLastMessage)) {
                    holder.oppoUsername.setText(list2.getsUsername());
                    holder.oppoUsername.setTextColor(Color.parseColor(list2.getsUserColor()));

                    holder.oppoUsername.setVisibility(View.VISIBLE);
                    sUserNameLastMessage = list2.getsUsername();
                } else {
                    holder.oppoUsername.setVisibility(View.GONE);
                }
                */
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists) {
        this.chatLists = chatLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout oppoLayout;
        private LinearLayout myLayout;
        private TextView oppoMessage;
        private TextView myMessage;
        private TextView oppoDateTime;
        private TextView myDateTime;

        private TextView oppoUsername;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            oppoDateTime = itemView.findViewById(R.id.oppoDateTime);
            myDateTime = itemView.findViewById(R.id.myDateTime);

            oppoUsername = itemView.findViewById(R.id.oppoUsername);
        }
    }
}













