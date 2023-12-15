package com.perseverance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText etCorreo;
    EditText etPassword;
    Button btnIniciarSesion;

    TextView btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistro = findViewById(R.id.txtRegistrarse);

        etCorreo.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etCorreo.setHintTextColor(Color.TRANSPARENT);
                etCorreo.setTextColor(Color.WHITE);
            } else {
                etCorreo.setHintTextColor(Color.WHITE);
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

        btnIniciarSesion.setOnClickListener(view -> {
            String correo = etCorreo.getText().toString();
            String password = etPassword.getText().toString();


            mAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    irAInicio();
                } else {
                        Toast.makeText(getApplicationContext(), "Correo o contraseña erroneos.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnRegistro.setOnClickListener(view -> irARegistro());

    }

    private void irAInicio(){
        Intent inicio = new Intent(MainActivity.this, InicioActivity.class);
        startActivity(inicio);
    }

    private void irARegistro(){
        Intent registro = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(registro);
    }

    public void onStart() {
        super.onStart();
        // Si el usuario accedió con anterioridad, no sale hasta que cierre sesión.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            irAInicio();
        }
    }

    public void onBackPressed() {
        finishAffinity();
    }

}