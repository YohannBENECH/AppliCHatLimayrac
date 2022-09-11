package com.example.applichatlimayrac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {

    EditText etEmail;
    Button btnRegister;
    Button btnLogin;
    Button btnSubmit;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Globals.FIREBASE_DATABASE_URL);

    protected void onCreate(Bundle savedInsatanceState) {
        super.onCreate(savedInsatanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.forgotPwd_email);
        btnSubmit = findViewById(R.id.forgotPwd_Submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            // Reset Password
            @Override
            public void onClick(View v) {
                String sEmail = etEmail.getText().toString().trim();

                if(PatternChecker.isEmailAddressValid(sEmail)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Email Sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    finish();
                } else {
                    Toast.makeText(ForgotPassword.this, "Please enter a valid Email Address", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Login - Go to activity
        btnLogin = findViewById(R.id.forgotPwd_loginButton);
        btnLogin.setOnClickListener(v -> startActivity(new Intent(ForgotPassword.this, Login.class)));

        // Register - Go to activity
        btnRegister = findViewById(R.id.forgotPwd_RegisterButton);
        btnRegister.setOnClickListener(v -> startActivity(new Intent(ForgotPassword.this, Register.class)));
    }
}
