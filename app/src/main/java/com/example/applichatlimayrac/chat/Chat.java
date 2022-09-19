package com.example.applichatlimayrac.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applichatlimayrac.AppareilPhoto;
import com.example.applichatlimayrac.Globals;
import com.example.applichatlimayrac.Login;
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
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.time.ZoneId;
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

        AndroidThreeTen.init(this);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView nameTV = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        final ImageView ivTakePicture = findViewById(R.id.btnTakePhoto);
        final ImageView ivPhotoTaken = findViewById(R.id.ivPhotoTaken);
        final RelativeLayout rlPhotoTaken = findViewById(R.id.photosBar);

        // By default Hide it - Photo taken Preview and its Bar
        ivPhotoTaken.setVisibility(View.GONE);
        rlPhotoTaken.setVisibility(View.GONE);

        // If there is a Picture Taken
        final String[] sBitmap = {getIntent().getStringExtra("imageTaken")};

        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        // Get data from Messages Adapter
        final String getUsername = getIntent().getStringExtra("username");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        final String getChatDateTime = getIntent().getStringExtra("chat_date_time");

        nameTV.setText(getUsername);
        Picasso.get().load(getProfilePic).into(profilePic);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getChatDateTime != null) {
                    if (getChatDateTime.isEmpty()) {
                        // Generate by default Date as a key for the message
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        sChatDateTime = now.format(formatter);
                    }
                }

                if(snapshot.hasChild("Messages")) {

                    // If first time, clear the list
                    //if(loadingFirstTime) {
                        chatLists.clear();
                    //}

                    for (DataSnapshot message : snapshot.child("Messages").getChildren()) {

                        // Parse Current Message
                        final String sMessageDateTime = message.getKey();
                        final String getUsername = message.child("Username").getValue(String.class);
                        final String getMessageTxt = message.child("MessageTxt").getValue(String.class);
                        final String getUsernameColor = message.child("UsernameColor").getValue(String.class);
                        final Boolean getHasImage = message.child("HasImage").getValue(Boolean.class);
                        final String getImageMsg = message.child("ImageMsg").getValue(String.class);

                        // If current Message is after the last one in Memory
                        if (getMessageTxt != null && getHasImage != null) {
                            MemoryData.saveLastMsgTS(sMessageDateTime, Chat.this);

                            loadingFirstTime = false;

                            UpdateChatView(getUsername, getHasImage, sMessageDateTime, getMessageTxt, getUsernameColor, getImageMsg);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Iff a photo has been taken
        try {
            if(sBitmap[0] != null) {
                // Convert Image
                byte[] decodedString = Base64.decode(sBitmap[0], Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Set Preview visible
                ivPhotoTaken.setVisibility(View.VISIBLE);
                rlPhotoTaken.setVisibility(View.VISIBLE);

                ivPhotoTaken.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ADD A PHOTO TO THE MESSAGE
        ivTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chat.this, AppareilPhoto.class));
            }
        });


        // SEND MESSAGE
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getTxtMessage = messageEditText.getText().toString();

                if(!getTxtMessage.trim().isEmpty() || sBitmap[0] != null) { // Avoid empty messages
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String sDatetime = now.format(formatter).toString();

                    //MemoryData.saveLastMsgTS(sDatetime, Chat.this);

                    // Write data in FireBase
                    databaseReference.child("Messages").child(sDatetime).child("MessageTxt").setValue(getTxtMessage);
                    databaseReference.child("Messages").child(sDatetime).child("Time").setValue(sDatetime.replace("-", "/"));
                    databaseReference.child("Messages").child(sDatetime).child("Username").setValue(Globals.LOGGED_USER_NAME);
                    databaseReference.child("Messages").child(sDatetime).child("UsernameColor").setValue(Globals.LOGGED_USER_COLOR);

                    // If there is an Image to include
                    boolean hasImage;
                    if(sBitmap[0] != null) {
                        databaseReference.child("Messages").child(sDatetime).child("HasImage").setValue(true);
                        databaseReference.child("Messages").child(sDatetime).child("ImageMsg").setValue(sBitmap[0]);

                        // Hide the Preview bar then
                        rlPhotoTaken.setVisibility(View.GONE);
                        hasImage = true;

                        // ResetPicture taken if there is not

                    } else {
                        databaseReference.child("Messages").child(sDatetime).child("HasImage").setValue(false);
                        databaseReference.child("Messages").child(sDatetime).child("ImageSource").setValue(getProfilePic);
                        hasImage = false;
                    }

                    // Update Chat View
                    UpdateChatView(getUsername, hasImage, sDatetime, getTxtMessage, Globals.LOGGED_USER_COLOR, sBitmap[0]);

                    // Clear Text in EditText
                    messageEditText.setText("");
                    sBitmap[0] = null;


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

    // -------------------------------------------------------------------------------------------------------------

    private void UpdateChatView(String sUsername, boolean hasImage, String sMessageDateTime, String getMessageTxt, String getUsernameColor, String sImage) {
        if (!(sUsername == null) && !sUsername.equals("")) {

            // If there is an image in the message
            if (!hasImage) {
                ChatList chatList = new ChatList(
                        sUsername,
                        sMessageDateTime,
                        getMessageTxt,
                        getUsernameColor,
                        false,
                        ""
                );

                chatLists.add(chatList);
                chatAdapter.updateChatList(chatLists);
                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
            }
            // If there is no image in the message
            if (hasImage && sImage != null) {
                ChatList chatList = new ChatList(
                        sUsername,
                        sMessageDateTime,
                        getMessageTxt,
                        getUsernameColor,
                        true,
                        sImage
                );

                chatLists.add(chatList);
                chatAdapter.updateChatList(chatLists);
                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
            }
        }
    }
}