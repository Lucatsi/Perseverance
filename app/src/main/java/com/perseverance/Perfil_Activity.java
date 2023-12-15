package com.perseverance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class Perfil_Activity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;
    private TextView btnRecuperarPassword, btnCambiarNumero, btnCambiarPassword;
    private TextView txtNombrePerfil;
    private TextView txtCorreoPerfil;
    private TextView txtNumeroPerfil;
    private ImageView imgEditPerfil;
    private FirebaseStorage fs;
    private ImageView imgProfileConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fs = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        txtNombrePerfil = findViewById(R.id.txtNombreUsuario);
        txtCorreoPerfil = findViewById(R.id.txtCorreo);
        txtNumeroPerfil = findViewById(R.id.txtNumero);
        imgProfileConfig = findViewById(R.id.imgProfile_config);
        imgEditPerfil = findViewById(R.id.btnEditFoto);
        btnCambiarPassword = findViewById(R.id.btnChangePassword);
        btnRecuperarPassword = findViewById(R.id.btnRestoratePassword);
        btnCambiarNumero = findViewById(R.id.btnChangeNumber);

        btnCambiarPassword.setOnClickListener(view -> iraCambiarPassword());
        btnCambiarNumero.setOnClickListener(view -> iraCambiarNumero());
        btnRecuperarPassword.setOnClickListener(view -> recuperarPassword());
        imgEditPerfil.setOnClickListener(view -> irEditarFoto());

        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String foto = documentSnapshot.getString("foto");

                //aqui empieza el if si la foto es nula
                if(foto != ""){
                    Glide.with(Perfil_Activity.this)
                            .load(foto)
                            .into(imgProfileConfig);
                }
                //aqui termina el if

            } else {
                Toast.makeText(this, "No se ha podido cargar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // No se pudo obtener el documento del usuario
            // Manejar el error
        });

        if (user != null) {
            String email = user.getEmail();

            // Detecta el nombre del usuario

            db.collection("usuarios")
                    .whereEqualTo("correo", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String correo = document.getString("correo");
                                String nombre = document.getString("nombre");

                                String numero = document.getString("numero");

                                txtNombrePerfil.setText(nombre);
                                txtCorreoPerfil.setText(correo);

                                if (numero != ""){
                                    txtNumeroPerfil.setText(numero);
                                }


                            }
                        } else {

                            Log.d("TAG", "Error obteniendo el documento.: ", task.getException());
                        }
                    });
        }

    }


    private void iraCambiarPassword(){
        Intent I = new Intent(Perfil_Activity.this, CambiarPasswordActivity.class);
        startActivity(I);
    }

    private void iraCambiarNumero(){
        Intent I = new Intent(Perfil_Activity.this, CambiarNumeroActivity.class);
        startActivity(I);
    }
    private void irEditarFoto(){
        Intent I = new Intent(Perfil_Activity.this, FotoPerfilActivity.class);
        startActivity(I);
    }

    private void recuperarPassword(){
        String emailAddress = user.getEmail();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo enviado!",
                                Toast.LENGTH_SHORT);
                        Log.d("TAG", "Email sent.");
                    }else{
                        Toast.makeText(this, "Error al enviar correo",
                                Toast.LENGTH_LONG);
                    }
                });
    }
}