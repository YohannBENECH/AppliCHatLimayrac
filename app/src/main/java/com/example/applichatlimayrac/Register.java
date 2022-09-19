package com.example.applichatlimayrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


import yuku.ambilwarna.AmbilWarnaDialog;

public class Register extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Globals.FIREBASE_DATABASE_URL);

    int iDefaultColor = 0;
    Button buttonColorPicker;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get view items
        final EditText username = findViewById(R.id.r_username);
        final EditText email = findViewById(R.id.r_email);
        final EditText password = findViewById(R.id.r_password);
        final AppCompatButton registerButton = findViewById(R.id.r_registerButton);
        final AppCompatButton loginButton = findViewById(R.id.r_loginButton);

        mAuth = FirebaseAuth.getInstance();

        // Color Picker stuff
        iDefaultColor = ContextCompat.getColor(Register.this, R.color.theme_color);
        buttonColorPicker = findViewById(R.id.colorPickerButton);
        buttonColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

        // Load Settings
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        // Check if user already logged in
        // TEST
        /*
        if(!MemoryData.getData(this).isEmpty()) {
            // Pass attributes to the next Activity
            Intent intent = new Intent(Register.this, MainActivity.class);
            intent.putExtra("userkey", MemoryData.getUserkey(this));
            intent.putExtra("email", MemoryData.getData(this));
            intent.putExtra("ussername", MemoryData.getUsername(this));
            intent.putExtra("password", "");
            intent.putExtra("usercolor", MemoryData.getUserColor(this));

            Globals.LOGGED_USER_NAME = MemoryData.getUsername(this);

            // Go to the next Activity and end the current one
            startActivity(intent);
            finish();
        }
        */

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show(); // Show loading

                final String sUsername = username.getText().toString();
                final String sEmail = email.getText().toString();
                final String sPassword = password.getText().toString();

                if(sUsername.isEmpty() || sEmail.isEmpty() || sPassword.isEmpty()) {
                    Toast.makeText(Register.this, "All fields required !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss(); // Stop showing loading
                } else {

                    // TEST Email format
                    if(PatternChecker.isEmailAddressValid(sEmail)) {

                        // Test Password format
                        if (PatternChecker.isPasswordValid(sPassword)) {

                            // --------------------------------------------------------------------
                            // Firebase Auth
                            mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(t -> {
                                            if(t.isSuccessful()) {
                                                // Set Username Color
                                                if(iDefaultColor == 0) {
                                                    Globals.LOGGED_USER_COLOR = "#404040";
                                                } else {
                                                    Globals.LOGGED_USER_COLOR = String.format("#%06X", (0xFFFFFF & iDefaultColor));
                                                }

                                                Toast.makeText(Register.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Register.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(Register.this, "Login Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(Register.this, "Registration Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // --------------------------------------------------------------------

                            // Registration Event
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    progressDialog.dismiss(); // Stop showing loading

                                    boolean isUsernameAlreadyExists = false;
                                    boolean isEmailAlreadyExists = false;

                                    // For Each User - Test if user already exists basing on its Username and Email Address
                                    for (DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {
                                        String getUsername = dataSnapshot.child("UserName").getValue(String.class);
                                        String getEmail = dataSnapshot.child("Mail").getValue(String.class);

                                        if (getUsername.equals(sUsername)) { // If Username already exists
                                            isUsernameAlreadyExists = true;
                                            Toast.makeText(Register.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                                        }
                                        if (getEmail.equals(sEmail)) { // If Email already exists
                                            isEmailAlreadyExists = true;
                                            Toast.makeText(Register.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // --------------------------------------------------------------------------
                                    if (!isUsernameAlreadyExists && !isEmailAlreadyExists) {

                                        // Generate UserKey based on DateTime
                                        String pattern = "yyyyMMddhhmmss";
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                        String sDateTime = simpleDateFormat.format(new Date());

                                        String sUserKey = "U" + sDateTime;
                                        System.out.println("UserKey : " + sUserKey);

                                        // If not, register it
                                        databaseReference.child("Users").child(sUserKey).child("UserName").setValue(sUsername);
                                        databaseReference.child("Users").child(sUserKey).child("Mail").setValue(sEmail);
                                        databaseReference.child("Users").child(sUserKey).child("PassWord").setValue(sPassword);
                                        databaseReference.child("Users").child(sUserKey).child("UserKey").setValue(sUserKey);

                                        String sHexColor = "";
                                        if(iDefaultColor == 0) {
                                            databaseReference.child("Users").child(sUserKey).child("UsernameColor").setValue("#404040");
                                        } else {
                                            sHexColor = String.format("#%06X", (0xFFFFFF & iDefaultColor));
                                            System.out.println("Color Picked : " + sHexColor);
                                            databaseReference.child("Users").child(sUserKey).child("UsernameColor").setValue(sHexColor);
                                        }

                                        // Save Userkey to memory
                                        MemoryData.saveUserkey(sUserKey, Register.this);
                                        // Save Email to memory
                                        MemoryData.saveData(sEmail, Register.this);
                                        // Save Username to memory
                                        MemoryData.saveUsername(sUsername, Register.this);
                                        // Save UserColor
                                        MemoryData.saveUserColor(sHexColor, Register.this);

                                        // Update logged User's Name global
                                        Globals.LOGGED_USER_NAME = sUsername;

                                        // Pass attributes to MainActivity
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        intent.putExtra("userkey", sUserKey);
                                        intent.putExtra("email", sEmail);
                                        intent.putExtra("ussername", sUsername);
                                        intent.putExtra("password", sPassword);
                                        intent.putExtra("usercolor", sHexColor);

                                        // Go to the next Activity and end the current one
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss(); // Stop showing loading
                                }
                            });
                        } else {
                            progressDialog.dismiss(); // Stop showing loading

                            Toast.makeText(Register.this, "Password not complex enough.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss(); // Stop showing loading

                        Toast.makeText(Register.this, "Email Address incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText email = findViewById(R.id.r_email);
                final EditText password = findViewById(R.id.r_password);

                String sEmail = email.getText().toString();
                String sPassword = password.getText().toString();

                if(sEmail != null && sPassword != null) {
                    if(!sEmail.isEmpty() && !sPassword.isEmpty()) {
                        Intent intent = new Intent(Register.this, Login.class);

                        intent.putExtra("email", sEmail);
                        intent.putExtra("password", sPassword);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(Register.this, Login.class));
                    }
                }
            }
        });
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, iDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // For that time, nothing
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                iDefaultColor = color;
                buttonColorPicker.setText("");
                buttonColorPicker.setBackgroundColor(iDefaultColor);
            }
        });
        colorPicker.show();
    }
}