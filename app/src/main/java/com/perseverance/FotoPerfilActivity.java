package com.perseverance;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.UUID;

public class FotoPerfilActivity extends AppCompatActivity {
    Button btnSubirFoto;
    FirebaseFirestore db;
    FirebaseUser user;

    private FirebaseStorage fs;
    private ImageView imgProfile;
    private Uri ImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_perfil);

        ActivityResultLauncher<Intent> photoRequest = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result!=null) {
                        // Obtener el intent con la foto seleccionada
                        ImagePath = result.getData().getData();
                        // Hacer algo con la foto
                        getImageInTheImageView();
                    }
                });

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fs = FirebaseStorage.getInstance();

        imgProfile = findViewById(R.id.imgProfile);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);


        btnSubirFoto.setOnClickListener(view -> uploadImage());
        imgProfile.setOnClickListener(view -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK);
            photoIntent.setType("image/*");
            photoRequest.launch(photoIntent);
        });

        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String foto = documentSnapshot.getString("foto");

                //aqui empieza el if si la foto es nula
                if(foto != ""){
                    Glide.with(FotoPerfilActivity.this)
                            .load(foto)
                            .into(imgProfile);
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

    }



    private void uploadImage(){
        Toast.makeText(this,"Subiendo imagen...",
                Toast.LENGTH_SHORT).show();
        fs.getReference("images/" + UUID.randomUUID().toString()).putFile(ImagePath).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        updateProfilePicture(task1.getResult().toString());
                    }
                });

                Toast.makeText(this,"Imagen subida correctamente!",
                        Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Subido correctamente!");
            }else {
                Toast.makeText(this,"Error!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfilePicture(String url) {
        String uid = user.getUid();
        db.collection("usuarios")
                .document(uid)
                .update("foto", url);

    }

    private void getImageInTheImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imgProfile.setImageBitmap(bitmap);
    }



}