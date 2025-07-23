package com.example.giroscopiopreguntas;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Actividad principal que implementa un juego de preguntas controlado por el giroscopio.
 * Los usuarios responden inclinando el dispositivo a izquierda (Falso) o derecha (Verdadero).
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscope;
    private TextView questionText, resultText, scoreText, instructionText;
    private View ballView;

    // Arreglo de preguntas y sus respuestas correspondientes
    private String[] questions = {
            "¿Panamá es la capital del país?",
            "¿En Bocas del Toro hay playas?",
            "¿Los pingüinos viven en Chiriquí?",
            "¿Colón está junto al mar?",
            "¿Darién está al lado de Colombia?",
            "¿Veraguas tiene volcanes?",
            "¿Los Santos es una isla?",
            "¿En la provincia de Panamá esta el canal?",
            "¿En Coclé neva?",
            "¿Panamá Oeste es la provincia más nueva?"
    };
    private boolean[] answers = {true, true, false, true, true, true, false, true, false, true};

    // Variables de control del juego
    private int currentQuestion = 0;
    private int score = 0;
    private boolean answered = false;
    private final float THRESHOLD = 0.9f; // Umbral de sensibilidad para detectar giro
    private float ballPosition = 0f;
    private final float MAX_BALL_MOVEMENT = 100f;

    private long lastTimestamp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Cargar layout XML

        // Obtener referencias de vistas del layout
        questionText = findViewById(R.id.questionText);
        resultText = findViewById(R.id.resultText);
        scoreText = findViewById(R.id.scoreText);
        instructionText = findViewById(R.id.instructionText);

        // Crear dinámicamente la bola como una vista circular azul
        RelativeLayout layout = findViewById(R.id.main_layout);
        ballView = new View(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100); // Tamaño de la bola
        params.addRule(RelativeLayout.CENTER_HORIZONTAL); // Centrada horizontalmente
        params.addRule(RelativeLayout.BELOW, R.id.instructionText); // Debajo del texto de instrucciones
        ballView.setLayoutParams(params);

        GradientDrawable shape = new GradientDrawable(); // Forma circular
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#3F51B5")); // Azul oscuro
        ballView.setBackground(shape);
        layout.addView(ballView); // Agregar la bola al layout

        // Inicializar sensores
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); // Obtener giroscopio
        }

        showQuestion(); // Mostrar primera pregunta
    }

    /**
     * Muestra la pregunta actual y prepara la interfaz para recibir respuesta
     */
    private void showQuestion() {
        if (currentQuestion < questions.length) {
            questionText.setText(questions[currentQuestion]); // Mostrar texto de pregunta
            resultText.setText(""); // Limpiar resultado anterior
            instructionText.setText("Incline el celular rapidamente a la izquierda para FALSO o a la derecha para VERDADERO");
            ballPosition = 0f; // Reiniciar posición de bola
            updateBallPosition(); // Dibujarla
            answered = false; // Permitir respuesta
        } else {
            // Fin del juego
            questionText.setText("¡Juego terminado!");
            instructionText.setText("");
            resultText.setText("Puntuación final: " + score + "/" + questions.length);
        }
    }

    private void updateBallPosition() {
        // Colocar la bola según la posición calculada, centrada horizontalmente
        ballView.setX(ballPosition + (getResources().getDisplayMetrics().widthPixels / 2f) - (ballView.getWidth() / 2f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME); // Activar sensor
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); // Desactivar sensor para ahorrar batería
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float y = event.values[1]; // Rotación en eje Y

            if (lastTimestamp != 0) {
                float dt = (event.timestamp - lastTimestamp) * 1.0f / 1_000_000_000.0f; // en segundos

                if (!answered) {
                    float delta = y * dt * -500f; // factor de escala ajustable
                    ballPosition += delta;
                    ballPosition = Math.max(Math.min(ballPosition, MAX_BALL_MOVEMENT), -MAX_BALL_MOVEMENT);
                    updateBallPosition();
                }

                if (!answered && Math.abs(y) > THRESHOLD) {
                    if (y > THRESHOLD) {
                        checkAnswer(true);
                        ballPosition = MAX_BALL_MOVEMENT;
                    } else if (y < -THRESHOLD) {
                        checkAnswer(false);
                        ballPosition = -MAX_BALL_MOVEMENT;
                    }
                    updateBallPosition();
                    answered = true;
                }
            }

            lastTimestamp = event.timestamp;
        }
    }


    private void checkAnswer(boolean userAnswer) {
        boolean correctAnswer = answers[currentQuestion];

        if (userAnswer == correctAnswer) {
            resultText.setText("¡Correcto!");
            resultText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            score++; // Aumentar puntuación
        } else {
            resultText.setText("Incorrecto");
            resultText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }

        // Actualizar texto de puntuación
        scoreText.setText("Puntuación: " + score + "/" + (currentQuestion + 1));

        // Avanzar a la siguiente pregunta tras 1.5 segundos
        questionText.postDelayed(() -> {
            currentQuestion++;
            showQuestion();
        }, 1500);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se usa en esta app, pero es obligatorio implementar
    }
}
