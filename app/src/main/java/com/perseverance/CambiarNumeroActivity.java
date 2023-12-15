package com.perseverance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CambiarNumeroActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    EditText etCambiarNumero;

    Button btnCambiarNumero;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_numero);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        btnCambiarNumero = findViewById(R.id.btnCambiarNumero);
        etCambiarNumero = findViewById(R.id.etCambiarNumero);

        etCambiarNumero.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etCambiarNumero.setHintTextColor(Color.TRANSPARENT);
                etCambiarNumero.setTextColor(Color.WHITE);
            } else {
                etCambiarNumero.setHintTextColor(Color.WHITE);
            }
        });

        btnCambiarNumero.setOnClickListener(view -> {
            String numero = etCambiarNumero.getText().toString();
            String email = user.getEmail();
            CollectionReference salasRef = db.collection("usuarios");
            Query query = salasRef.whereEqualTo("correo", email);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("numero", numero);
                    for (DocumentSnapshot document : documents) {
                        DocumentReference docRef = document.getReference();
                        docRef.update(updates).addOnSuccessListener(aVoid -> {
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                        }).addOnFailureListener(e -> {
                            Log.w("TAG", "Error updating document", e);
                        });
                    }
                } else {
                    Log.w("TAG", "Error getting documents", task.getException());
                }
            });
        });

    }
}