package com.example.giroscopiopreguntas;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Actividad que muestra en pantalla los valores del giroscopio en tiempo real
public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager; // Gestor de sensores
    private Sensor gyroscope;            // Sensor giroscopio
    private TextView sensorText;         // TextView para mostrar los valores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Llama a la implementación base
        setContentView(R.layout.activity_sensor); // Establece el layout XML para esta actividad

        sensorText = findViewById(R.id.sensorText); // Enlaza el TextView desde el layout

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // Obtiene el servicio de sensores
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // Obtiene el giroscopio si está disponible
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Se registra el listener para comenzar a recibir eventos del giroscopio
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Si el evento proviene del giroscopio
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0]; // Rotación en eje X
            float y = event.values[1]; // Rotación en eje Y
            float z = event.values[2]; // Rotación en eje Z

            // Formatea los valores con dos decimales y los muestra en pantalla
            String sensorData = String.format(
                    "Datos del Giroscopio:\nX: %.2f\nY: %.2f\nZ: %.2f",
                    x, y, z
            );
            sensorText.setText(sensorData); // Actualiza el TextView con los datos
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Método obligatorio pero no se usa en esta app
    }
}
