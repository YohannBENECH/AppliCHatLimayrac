package com.example.applichatlimayrac.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applichatlimayrac.Globals;
import com.example.applichatlimayrac.MainActivity;
import com.example.applichatlimayrac.MemoryData;
import com.example.applichatlimayrac.PatternChecker;
import com.example.applichatlimayrac.R;
import com.example.applichatlimayrac.Register;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Globals.FIREBASE_DATABASE_URL);

    private final List<ChatList> chatLists = new ArrayList<>();

    private String sChatDateTime = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView nameTV = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);

        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        // Get data from Messages Adapter
        final String getUsername = getIntent().getStringExtra("username");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        final String getChatDateTime = getIntent().getStringExtra("chat_date_time");
        final String sUserColor = getIntent().getStringExtra("usercolor");

        nameTV.setText(getUsername);
        Picasso.get().load(getProfilePic).into(profilePic);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getChatDateTime.isEmpty()){
                    // Generate by default Date as a key for the message
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    sChatDateTime = now.format(formatter);
                }

                if(snapshot.hasChild("Messages")) {

                    if(loadingFirstTime) {
                        chatLists.clear();
                    }

                    for(DataSnapshot message : snapshot.child("Messages").getChildren()) {

                        // if(PatternChecker.isMessageValid(message)) {

                            // Parse Current Message
                            final String sMessageDateTime = message.getKey();
                            final String getUsername = message.child("Username").getValue(String.class);
                            final String getMessageTxt = message.child("MessageTxt").getValue(String.class);
                            final String getImageSource = message.child("ImageSource").getValue(String.class);
                            // final String getDateTime = message.child("Time").getValue(String.class);
                            final String getUsernameColor = message.child("UsernameColor").getValue(String.class);

                            // Parse its DateTime
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime dateTimeCurrentMsg = LocalDateTime.parse(sMessageDateTime, formatter);

                            String sLastMsgDateTime = MemoryData.getLastMessageTS(Chat.this);
                            LocalDateTime lastMsgDateTime = null;
                            if(sLastMsgDateTime.isEmpty()) {
                                MemoryData.saveLastMsgTS(sMessageDateTime, Chat.this);
                                lastMsgDateTime = dateTimeCurrentMsg;
                            } else {
                                lastMsgDateTime = LocalDateTime.parse(sLastMsgDateTime, formatter);
                            }

                            // If current Message is after the last one in Memory
                            //if((loadingFirstTime || dateTimeCurrentMsg.isAfter(lastMsgDateTime)) && getMessageTxt != null) {
                            if(getMessageTxt != null) {
                                MemoryData.saveLastMsgTS(sMessageDateTime, Chat.this);

                                loadingFirstTime = false;

                                if (!(getUsername == null) && !getUsername.equals("")) {
                                    ChatList chatList = new ChatList(
                                            getUsername,
                                            sMessageDateTime,
                                            getMessageTxt,
                                            getUsernameColor
                                    );

                                    chatLists.add(chatList);
                                    chatAdapter.updateChatList(chatLists);
                                    chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                }
                            }
                            //}
                        // }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getTxtMessage = messageEditText.getText().toString();

                if(!getTxtMessage.trim().isEmpty()) { // Avoid empty messages
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String sDatetime = now.format(formatter).toString();

                    //MemoryData.saveLastMsgTS(sDatetime, Chat.this);

                    // Write data in FireBase
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("HasImage").setValue(false);
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("ImageSource").setValue(getProfilePic);
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("MessageTxt").setValue(getTxtMessage);
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("Time").setValue(sDatetime.replace("-", "/"));
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("Username").setValue(Globals.LOGGED_USER_NAME);
                    databaseReference.child("Messages").child(now.format(formatter).toString()).child("UsernameColor").setValue(sUserColor);

                    // Clear Text in EditText
                    messageEditText.setText("");
                } else {
                    Toast.makeText(Chat.this, "Empty Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Chat.this, MainActivity.class));
            }
        });
    }
}