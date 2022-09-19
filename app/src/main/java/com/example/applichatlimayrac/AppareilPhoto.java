package com.example.applichatlimayrac;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.applichatlimayrac.chat.Chat;

import java.io.ByteArrayOutputStream;


public class AppareilPhoto extends AppCompatActivity {

    ImageView ivPhoto;
    Button btnTakePhoto;
    ImageView ivValidatePhoto;
    ImageView ivRefusePhoto;

    protected void onCreate(Bundle savedInsatanceState) {
        super.onCreate(savedInsatanceState);
        setContentView(R.layout.activity_appareil_photo);

        ivPhoto = findViewById(R.id.APhoto_ivPhoto);
        btnTakePhoto = findViewById(R.id.APhoto_btnTakePhoto);
        ivValidatePhoto = findViewById(R.id.APhoto_ivValidatePhoto);
        ivRefusePhoto = findViewById(R.id.APhoto_ivRefusePhoto);

        // Hide those 2 buttons because they are useful only once the picture is taken
        ivValidatePhoto.setVisibility(View.GONE);
        ivRefusePhoto.setVisibility(View.GONE);

        // Request for permission
        if(ContextCompat.checkSelfPermission(AppareilPhoto.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AppareilPhoto.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        // Take Photo if button clicked
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        ivRefusePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppareilPhoto.this, Chat.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            ivPhoto.setImageBitmap(bitmap);

            // Show the validation buttons
            ivValidatePhoto.setVisibility(View.VISIBLE);
            ivRefusePhoto.setVisibility(View.VISIBLE);

            // Validate Photo - Pass it to the Chat Activity
            ivValidatePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Convert Image - From Bitmap to String
                    String encodedBitmap = encodeBitmap(bitmap);

                    // Pass attributes to the next Activity
                    Intent intent = new Intent(AppareilPhoto.this, Chat.class);
                    intent.putExtra("imageTaken", encodedBitmap);

                    // Go to the next Activity and end the current one
                    startActivity(intent);
                    finish();
                }
            });

            // Refuse Photo - Retake photo
            ivRefusePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPhoto.setImageBitmap(null);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            });


        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    public String encodeBitmap(Bitmap bitmap) {
        String encodedBitmap;

        // Convert Image - From Bitmap to String
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedBitmap;
    }
}












