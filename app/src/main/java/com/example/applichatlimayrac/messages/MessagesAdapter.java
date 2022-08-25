package com.example.applichatlimayrac.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applichatlimayrac.R;
import com.example.applichatlimayrac.chat.Chat;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> listMessages;
    private final Context context;

    public MessagesAdapter(List<MessagesList> listMessages, Context context) {
        this.listMessages = listMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {

        MessagesList list2 = listMessages.get(position);

        if(list2.getsProfilePic() != null){
            if(!list2.getsProfilePic().isEmpty()) {
                Picasso.get().load(list2.getsProfilePic()).into(holder.profilePic);
            }
        } else {
            Picasso.get().load("android.ressource:res/drawable-v24/limayrac.png").into(holder.profilePic);
        }

        holder.username.setText(list2.getsUsername());
        holder.lastMessage.setText(list2.getsLastMessage());

        if(list2.getiUnseenMessages() == 0) {
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getiUnseenMessages()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_color_80));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("username", list2.getsUsername());
                intent.putExtra("profile_pic", list2.getsProfilePic());
                intent.putExtra("chat_date_time", list2.getsDateTime());
                intent.putExtra("usercolor", list2.getsUserColor());

                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error : ");
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateData(List<MessagesList> listMessages) {
        this.listMessages = listMessages;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profilePic;
        private TextView username;
        private TextView lastMessage;
        private TextView unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseen_messages);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }
}
