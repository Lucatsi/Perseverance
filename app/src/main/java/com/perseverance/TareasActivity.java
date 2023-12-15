package com.perseverance;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class TareasActivity extends AppCompatActivity {

    final String host = "a61153b0d33242d680c2af8b7cd72932.s2.eu.hivemq.cloud";
    final String username = "usuario";
    final String password = "usuario123";

    String topic = "crear tarea";

    List<ItemTarea> elements;
    private EditText editTarea;
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private Mqtt3AsyncClient client;
    FirebaseFirestore db;

    private static final String CHANNEL_ID = "canal";
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);
        editTarea = findViewById(R.id.editTarea);
        recyclerView = findViewById(R.id.recicler);
        db = FirebaseFirestore.getInstance();


        elements = new ArrayList<>();


        init();
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost(host)
                .serverPort(8883)
                .useSslWithDefaultConfig()
                .buildAsync();
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.d("errorMqtt", "No se pudo conectar");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Conexion fallida", Toast.LENGTH_SHORT).show();
                        });

                    } else {

                        client.subscribeWith()
                                .topicFilter(topic)
                                .qos(MqttQos.AT_LEAST_ONCE)
                                .send()
                                .whenComplete((subAck, subscriptionThrowable) -> {
                                    if (subscriptionThrowable != null) {
                                        Log.d("errorMqtt", "No se pudo suscribir al topic");
                                    } else {
                                        Log.d("successMqtt", "Suscripción exitosa al topic");
                                    }
                                });

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        });
                    }
                });


    }


    public void init() {
        adapter = new ListAdapter(elements, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Button btnCrearTarea = findViewById(R.id.btnCrear); // Referencia al botón
        btnCrearTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener texto del EditText
                String titulo = editTarea.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String fechaActual = dateFormat.format(calendar.getTime());


                elements.add(new ItemTarea(titulo, fechaActual, null, null));

                adapter.setItems(elements);

                adapter.notifyDataSetChanged();
                Map<String, Object> tarea = new HashMap<>();
                tarea.put("titulo", titulo);
                tarea.put("fecha", fechaActual);

                db.collection("tareas")
                        .add(tarea)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Documento agregado con ID: " + documentReference.getId());

                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error al agregar documento", e);
                        });

                client.publishWith()
                        .topic(topic)
                        .payload(UTF_8.encode("Hello"))
                        .send()
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                Log.e("MQTT", "Error al publicar el mensaje: " + throwable.getMessage());
                            } else {
                                Toast.makeText(TareasActivity.this, "tarea inscrita", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }


}