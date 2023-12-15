package com.perseverance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class CambiarPasswordActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    EditText etCambiarPassword;

    Button btnCambiarPassowrd;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        btnCambiarPassowrd = findViewById(R.id.btnCambiarPassword);
        etCambiarPassword = findViewById(R.id.etCambiarPassword);

        etCambiarPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etCambiarPassword.setHintTextColor(Color.TRANSPARENT);
                etCambiarPassword.setTextColor(Color.WHITE);
            } else {
                etCambiarPassword.setHintTextColor(Color.WHITE);
            }
        });

        btnCambiarPassowrd.setOnClickListener(view -> {
            String password = etCambiarPassword.getText().toString();
            String newPassword = password;

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User password updated.");
                        }
                    });
        });


    }
}