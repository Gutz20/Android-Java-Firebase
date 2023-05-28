package com.paqta.paqtafood.screens.cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paqta.paqtafood.R;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    StepView stepView;
    TextView stepTextView;
    Button btnReservar, btnComprar;
    RecyclerView rycrCartPlatillos, rycrCartBebidasPostres;
    LinearLayout layoutContaint;

    int stepIndex = 0;
    String[] stepsTexts = {"CARRITO", "ENTREGA", "METODO DE PAGO"};
    List<TextView> textViewList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        btnReservar = root.findViewById(R.id.btnReservar);
        btnComprar = root.findViewById(R.id.btnComprar);

        rycrCartPlatillos = root.findViewById(R.id.cartPlatillos);
        rycrCartBebidasPostres = root.findViewById(R.id.cartBebidasPostres);
        layoutContaint = root.findViewById(R.id.linearLayoutContaint);

        stepTextView = root.findViewById(R.id.stepTextView);
        stepView = root.findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        btnComprar.setOnClickListener(v -> {
            stepIndex++;
            if (stepIndex < stepsTexts.length) {
                stepTextView.setText(stepsTexts[stepIndex]);
                stepView.go(stepIndex, true);

                btnReservar.setVisibility(View.GONE);
//                rycrCartPlatillos.setVisibility(View.GONE);
//                rycrCartBebidasPostres.setVisibility(View.GONE);

                layoutContaint.removeAllViews();

                TextView textViewDireccion = new TextView(getActivity());
                textViewDireccion.setText("Direccion");

                TextView textViewPhone = new TextView(getActivity());
                textViewPhone.setText("Telefono");

                TextView textViewRef = new TextView(getActivity());
                textViewRef.setText("Referencia");

                textViewList.add(textViewDireccion);
                textViewList.add(textViewPhone);
                textViewList.add(textViewRef);
                textViewList.forEach(textView -> {
                    textView.setTextColor(getResources().getColor(R.color.colorBlanco, getActivity().getTheme()));
                    layoutContaint.addView(textView);
                });




            }
        });

//        nextStep();
        return root;
    }

    private void nextStep() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stepIndex++;
                if (stepIndex < stepsTexts.length) {
                    stepTextView.setText(stepsTexts[stepIndex]);
                    stepView.go(stepIndex, true);
                    nextStep();
                }
            }
        }, 3000);
    }
}