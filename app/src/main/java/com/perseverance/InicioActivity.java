package com.perseverance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class InicioActivity extends AppCompatActivity {

    Button btnLogout, btnGoToMQTT, btnTareas;

    TextView btnGoToPerfil;

    FirebaseFirestore db;
    FirebaseUser user;

    private TextView txtNombreInicio;
    private FirebaseStorage fs;
    private ImageView imgProfile_inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fs = FirebaseStorage.getInstance();

        imgProfile_inicio = findViewById(R.id.imgProfile_inicio);
        txtNombreInicio = findViewById(R.id.txtNombreInicio);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> cerrarSesion());

        btnGoToPerfil = findViewById(R.id.btnConfig);
        btnGoToPerfil.setOnClickListener(view -> configurationIntent());


        btnTareas = findViewById(R.id.btnTareas);
        btnTareas.setOnClickListener(view -> TareasIntent());


        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String foto = documentSnapshot.getString("foto");

                //aqui empieza el if si la foto es nula
                if(foto != ""){
                    Glide.with(InicioActivity.this)
                            .load(foto)
                            .into(imgProfile_inicio);
                }
                //aqui termina el if

            } else {
                Toast.makeText(this, "No se ha podido cargar la imagen",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {

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
                                String nombre = document.getString("nombre");
                                    txtNombreInicio.setText(nombre);
                            }
                        } else {

                            Log.d("TAG", "Error obteniendo el documento.: ", task.getException());
                        }
                    });
        }

    }

    private void configurationIntent(){
        Intent I = new Intent(InicioActivity.this, Perfil_Activity.class);
        startActivity(I);
    }

    private void TareasIntent(){
        Intent I = new Intent(InicioActivity.this, TareasActivity.class);
        startActivity(I);
    }
    private void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        Intent I = new Intent(InicioActivity.this, MainActivity.class);
        startActivity(I);
    }

}