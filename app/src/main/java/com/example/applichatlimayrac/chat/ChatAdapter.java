package com.example.applichatlimayrac.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            // -------------------------------------------------------------------------------------
            // If message's Username equals to currently logged Username
            if(list2.getsUsername().equals(sUsername)) {
                holder.myLayout.setVisibility(View.VISIBLE);
                holder.oppoLayout.setVisibility(View.GONE);

                holder.myDateTime.setText(list2.getsDateTime());

                    // sUserNameLastMessage = list2.getsUsername();

                // If there is no text in the message ----------------------
                if(list2.getsMessage().equals(null)) {
                    holder.myMessage.setVisibility(View.GONE);
                } else {
                    holder.myMessage.setVisibility(View.VISIBLE);
                    holder.myMessage.setText(list2.getsMessage());
                }

                // IF THERE IS AN IMAGE ------------------------------------
                if(list2.getHasImage() && !list2.getsImage().isEmpty()) {

                    // Convert Image
                    byte[] decodedString = Base64.decode(list2.getsImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    // Put it in the View
                    if(holder.myImage != null) {
                        holder.myImage.setVisibility(View.VISIBLE);
                        holder.myImage.setImageBitmap(decodedByte);

                        // Adjust Image Size
                        holder.myImage.requestLayout();
                        holder.myImage.getLayoutParams().height = 700;
                        holder.myImage.getLayoutParams().width = 700;
                    }
                } else { // NO IMAGE IN MESSAGE
                    // Hide the Image view if there is no image to show
                    holder.myImage.setVisibility(View.GONE);
                }
                // ---------------------------------------------------------

            // -------------------------------------------------------------------------------------
            } else { // NOT A MESSAGE FROM CURRENT USER
                holder.myLayout.setVisibility(View.GONE);
                holder.oppoLayout.setVisibility(View.VISIBLE);

                holder.oppoDateTime.setText(list2.getsDateTime());

                // Manage Users Colors
                holder.oppoUsername.setText(list2.getsUsername());
                if(list2.getsUserColor() != null) {
                    holder.oppoUsername.setTextColor(Color.parseColor(list2.getsUserColor()));
                }

                if(list2.getsMessage().equals(null)) {
                    holder.oppoMessage.setVisibility(View.GONE);
                } else {
                    holder.oppoMessage.setVisibility(View.VISIBLE);
                    holder.oppoMessage.setText(list2.getsMessage());
                }

                // IF THERE IS AN IMAGE
                if(list2.getHasImage() && !list2.getsImage().isEmpty()) {

                    // Convert Image
                    byte[] decodedString = Base64.decode(list2.getsImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    // Put it in the view
                    if(holder.oppoImage != null) {
                        holder.oppoImage.setVisibility(View.VISIBLE);
                        holder.oppoImage.setImageBitmap(decodedByte);

                        // Adjust Image size
                        holder.oppoImage.requestLayout();
                        holder.oppoImage.getLayoutParams().height = 700;
                        holder.oppoImage.getLayoutParams().width = 700;
                    }
                } else {
                    // Hide the Image view if there is no image to show
                    holder.oppoImage.setVisibility(View.GONE);
                }

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
            // -------------------------------------------------------------------------------------
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
        private ImageView oppoImage;
        private ImageView myImage;

        private TextView oppoUsername;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            oppoDateTime = itemView.findViewById(R.id.oppoDateTime);
            myDateTime = itemView.findViewById(R.id.myDateTime);

            oppoImage = itemView.findViewById(R.id.oppoImage);
            myImage = itemView.findViewById(R.id.myImage);

            oppoUsername = itemView.findViewById(R.id.oppoUsername);
        }
    }
}













