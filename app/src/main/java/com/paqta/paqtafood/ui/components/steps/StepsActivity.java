package com.paqta.paqtafood.ui.components.steps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.paqta.paqtafood.R;
import com.shuhart.stepview.StepView;

public class StepsActivity extends AppCompatActivity {

    StepView stepView;
    TextView stepTextView;
    TextView descriptionTextView;

    int stepIndex = 0;
    String[] stepsTexts = {"Paso 1", "Paso 2", "Paso 3", "Paso 4"};
    String[] descriptionTexts = {
            "Procesando pedido a nuestro local",
            "Pedido en proceso de envio",
            "Enviando pedido, localizalo en nuestro mapa",
            "Pedido recibido, disfrutalo!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        stepTextView = findViewById(R.id.stepTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        stepView = findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(4)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();


        nextStep();
    }

    public void nextStep() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stepIndex++;

                if (stepIndex < stepsTexts.length) {
                    stepTextView.setText(stepsTexts[stepIndex]);
                    descriptionTextView.setText(descriptionTexts[stepIndex]);
                    stepView.go(stepIndex, true);
                    nextStep();
                }
            }
        }, 3000);
    }
}