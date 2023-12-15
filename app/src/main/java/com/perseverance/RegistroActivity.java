package com.perseverance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    EditText etCorreo;
    EditText etPassword;
    EditText etNombre;
    Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etCorreo = findViewById(R.id.etCorreo);
        etNombre = findViewById(R.id.etNombre);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        etCorreo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etCorreo.setHintTextColor(Color.TRANSPARENT);
                etCorreo.setTextColor(Color.WHITE);
            } else {
                etCorreo.setHintTextColor(Color.WHITE);
            }
        });

        etNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etNombre.setHintTextColor(Color.TRANSPARENT);
                etNombre.setTextColor(Color.WHITE);
            } else {
                etNombre.setHintTextColor(Color.WHITE);
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etPassword.setHintTextColor(Color.TRANSPARENT);
                etPassword.setTextColor(Color.WHITE);
            } else {
                etPassword.setHintTextColor(Color.WHITE);
            }
        });

        btnRegistrarse.setOnClickListener(view -> {

            String correo = etCorreo.getText().toString();
            String nombre = etNombre.getText().toString();
            String password = etPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Si se logra registrar
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre);
                            usuario.put("correo", correo);
                            usuario.put("foto", "");
                            usuario.put("numero", "");

                            String uid = mAuth.getUid();

                            db.collection("usuarios")
                                    .document(uid)
                                    .set(usuario)
                                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Documento añadido correctamente" ))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error al añadir el documento", e));

                            iniciarSesion();
                        } else {
                            // Si el usuario falla en algo, aparece este correo
                            Toast.makeText(this, "Correo ya registrado o inválido",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });



    }

    private void iniciarSesion(){
        Intent inicio = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(inicio);
        Toast.makeText(this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
    }

}