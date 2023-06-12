package com.paqta.paqtafood.ui.user.cart.components;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.paqta.paqtafood.R;

public class ThirdStepCartFragment extends Fragment {

    MaterialCardView cardPaymentVisa, cardPaymentEWallet, cardPaymentPaypal, cardPaymentPagoEfectivo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_third_step_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardPaymentVisa = view.findViewById(R.id.cardPaymentVisa);
        cardPaymentEWallet = view.findViewById(R.id.cardPaymentEWallet);
        cardPaymentPaypal = view.findViewById(R.id.cardPaymentPaypal);
        cardPaymentPagoEfectivo = view.findViewById(R.id.cardPaymentPagoEfectivo);

        cardPaymentVisa.setOnClickListener(v -> {
            // Le da un borde, podria aplicarse una animación de seleccion con un borde verde
//            cardPaymentVisa.setStrokeWidth(20);
//            cardPaymentVisa.setStrokeColor(Color.BLACK);
            Snackbar.make(v, "Pago con tarjeta", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNavigationView)
                    .show();
        });

        cardPaymentEWallet.setOnClickListener(v -> {
            Snackbar.make(v, "Pago con yape o plin", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNavigationView)
                    .show();
        });

        cardPaymentPaypal.setOnClickListener(v -> {
            Snackbar.make(v, "Pago con paypal", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNavigationView)
                    .show();
        });

        cardPaymentPagoEfectivo.setOnClickListener(v -> {
            Snackbar.make(v, "Pago con pago efectivo", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNavigationView)
                    .show();
        });


    }
}