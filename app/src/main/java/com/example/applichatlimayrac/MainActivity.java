package com.example.applichatlimayrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.example.applichatlimayrac.messages.MessagesAdapter;
import com.example.applichatlimayrac.messages.MessagesList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Globals.FIREBASE_DATABASE_URL);

    private final List<MessagesList> messagesLists = new ArrayList<>();

    private String sUserkey;
    private String sEmail;
    private String sUsername;
    private String sPassword;
    private String sUserColor;

    private int unseenMessages = 0;
    private String lastMessage = "";
    private String sChatDateTime = "";

    private boolean dataSet = false;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleImageView userProfilePic = findViewById(R.id.userProfilePic);

        Button btnLogout = findViewById(R.id.logoutButton);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        // Get intent data from Registration Activity
        sUserkey = getIntent().getStringExtra("userkey");
        sEmail = getIntent().getStringExtra("email");
        sUsername = getIntent().getStringExtra("username");
        sPassword = getIntent().getStringExtra("password");
        sUserColor = getIntent().getStringExtra("usercolor");

        mAuth = FirebaseAuth.getInstance();

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter to RecylerView
        messagesAdapter = new MessagesAdapter(messagesLists, MainActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);

        // Loading Settings
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Get profile pic from firebase DB
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String sProfilePicUrl = "";
                try {
                    sProfilePicUrl = snapshot.child("Users").child(sUserkey).child("ImageSource").getValue(String.class);
                } catch (Exception e) {
                    sProfilePicUrl = "";
                }

                // If profile pic Url isn t empty
                if(sProfilePicUrl != null) {
                    if(!sProfilePicUrl.isEmpty()) {
                        // Set profile pic
                        Picasso.get().load(sProfilePicUrl).into(userProfilePic);
                    }
                }

                progressDialog.dismiss(); // Stop showing loading
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss(); // Stop showing loading
            }
        });

        // ------------------------------------------------------------------------

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Clean contatct list
                messagesLists.clear();
                unseenMessages = 0;
                lastMessage = "";
                sChatDateTime = "";
                dataSet = false;

                final String getProfilePic = "https://drive.google.com/file/d/1g6U_voamFu8W9t1DqFTtRseQac3mJCWy/view?usp=sharing";

                databaseReference.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int getChatCounts = (int) snapshot.getChildrenCount();

                        if(getChatCounts > 0) {
                            // For each Message in FB
                            for(DataSnapshot chatDataSnapshot : snapshot.getChildren()) {
                                String sGetDateTimeCurrentMsg = chatDataSnapshot.getKey();
                                sChatDateTime = sGetDateTimeCurrentMsg;
                                String sGetUsernameCurrentMsg = chatDataSnapshot.child("Username").getValue(String.class);

                                // Get Last Message Date
                                final String getLastSeenMessage = MemoryData.getLastMessageTS(MainActivity.this);

                                // Parse to be comparable - Current Message and the last one in Memory

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime dateTimeCurrentMsg = LocalDateTime.parse(sChatDateTime, formatter);

                                if(!getLastSeenMessage.isEmpty()) {
                                    LocalDateTime dateTimeLastSeenMsg = LocalDateTime.parse(getLastSeenMessage, formatter);

                                    // If a message's date is newer than the last seen one, then IT S A NEW MESSAGE !
                                    lastMessage = chatDataSnapshot.child(sGetDateTimeCurrentMsg).child("MessageTxt").getValue(String.class);
                                    if(dateTimeCurrentMsg.isAfter(dateTimeLastSeenMsg)) {
                                        unseenMessages++;
                                    }
                                } else {
                                    unseenMessages++;
                                }


                            }
                        }
                        if (!dataSet) {
                            dataSet = true;
                            MessagesList messagesList = new MessagesList("Limayrac", "", getProfilePic, 0, sChatDateTime, sUserColor);
                            messagesLists.add(messagesList);
                            messagesAdapter.updateData(messagesLists);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                startActivity(new Intent(
                        MainActivity.this,
                        Register.class
                ));
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser == null) {
            startActivity(new Intent(
                    MainActivity.this,
                    Register.class
            ));
        }
    }
}









