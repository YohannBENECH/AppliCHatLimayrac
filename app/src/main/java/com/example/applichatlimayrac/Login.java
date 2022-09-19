package com.example.applichatlimayrac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Globals.FIREBASE_DATABASE_URL);

    EditText etLoginEmail;
    EditText etLoginPassword;
    Button btnRegister;
    Button btnLogin;
    TextView tvForgotPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInsatanceState) {
        super.onCreate(savedInsatanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.login_email);
        etLoginPassword = findViewById(R.id.login_password);
        btnRegister = findViewById(R.id.login_RegisterButton);
        btnLogin = findViewById(R.id.login_loginButton);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        // User just registered ? If yes, preset email
        String sEmail = getIntent().getStringExtra("email");
        String sPassword = getIntent().getStringExtra("password");


        if(sEmail != null && sPassword != null) {
            if (!sEmail.isEmpty() && !(sPassword.isEmpty())) {
                etLoginEmail.setText(sEmail);
                etLoginPassword.setText(sPassword);
            }
        }

        // Forgot Password - Go to activity
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(Login.this, ForgotPassword.class)));

        // Login User
        btnLogin.setOnClickListener(v -> loginUser());

        // Register - Go to activity
        btnRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }

    private void loginUser() {
        String sEmail = etLoginEmail.getText().toString();
        String sPassword = etLoginPassword.getText().toString();

        String sUsername = "";

        if(PatternChecker.isEmailAddressValid(sEmail)
        && PatternChecker.isPasswordValid(sPassword)) {
            mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    if(databaseReference != null) {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {
                                    String sCurrentUserEmail = dataSnapshot.child("Mail").getValue(String.class);
                                    String sCurrentUserPassword = dataSnapshot.child("PassWord").getValue(String.class);
                                    String sUserName = dataSnapshot.child("UserName").getValue(String.class);
                                    String sUserNameColor = dataSnapshot.child("UsernameColor").getValue(String.class);

                                    if(sCurrentUserEmail.equals(sEmail)
                                    && sCurrentUserPassword.equals(sPassword)) {
                                        Globals.LOGGED_USER_NAME = sUserName;
                                        Globals.LOGGED_USER_COLOR = sUserNameColor;
                                    }
                                }

                                startActivity(new Intent(Login.this, MainActivity.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(Login.this, "Login Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
