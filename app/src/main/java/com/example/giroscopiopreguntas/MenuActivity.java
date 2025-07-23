package com.example.giroscopiopreguntas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

// Clase del menú principal de la app
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Obtiene los botones desde el layout por su ID
        Button btnJuego = findViewById(R.id.btnJuego);   // Botón para ir al juego de preguntas
        Button btnSensor = findViewById(R.id.btnSensor); // Botón para ir a una actividad que muestra info del sensor

        // Listener para el botón del juego
        btnJuego.setOnClickListener(v -> {
            // Inicia la actividad principal (MainActivity)
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        });

        // Listener para el botón del sensor
        btnSensor.setOnClickListener(v -> {
            // Inicia la actividad del sensor (SensorActivity)
            startActivity(new Intent(MenuActivity.this, SensorActivity.class));
        });
    }
}