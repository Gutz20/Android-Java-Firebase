package com.paqta.paqtafood.ui.user.cart.components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.paqta.paqtafood.R;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.shuhart.stepview.StepView;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ThirdStepCartFragment extends Fragment {

    StepView stepView;
    MaterialCardView cardPaymentVisa, cardPaymentPaypal, cardPaymentPagoEfectivo;

    // Visa
    MaterialButton btnComprar;
    String SECRET_KEY = "sk_test_51NHfg9EfcKxNGCdShwNyzFnlcs0w19Hro6Rn5vUpDqAY6VxHgJ0nGKFdnCWxd99NNNcxo481OEqXitt42VKpNnBY00vULOcUlX";
    String PUBLISH_KEY = "pk_test_51NHfg9EfcKxNGCdSWG6saQG9uV7RfXRi8pdUZfN5qwRMIDH28yfFRbhS9mwLyNxDQLhxQIeKgYsqS6ilRey8pW1R00P5Nh0ODJ";
    PaymentSheet paymentSheet;
    String ClientSecret;
    Integer PAYPAL_REQUEST_CODE = 123;
    String customerID;
    String EphericalKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_third_step_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        initComponents();
    }

    private void initUI(View view) {
        cardPaymentVisa = view.findViewById(R.id.cardPaymentVisa);
        cardPaymentPaypal = view.findViewById(R.id.cardPaymentPaypal);
        cardPaymentPagoEfectivo = view.findViewById(R.id.cardPaymentPagoEfectivo);
        btnComprar = view.findViewById(R.id.btnComprar);
        stepView = view.findViewById(R.id.step_view);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);
    }

    private void initComponents() {
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();
        stepView.go(2, true);
        requestEtherical();
        cardPaymentVisa.setOnClickListener(v -> paymentFlow());
        cardPaymentPaypal.setOnClickListener(v -> showMessage(v, "Pago en paypal"));
        cardPaymentPagoEfectivo.setOnClickListener(v -> showMessage(v, "Pago efectivo"));
        btnComprar.setOnClickListener(v -> paymentFlow());
    }

    private void requestEtherical() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", response -> {
            try {
                JSONObject object = new JSONObject(response);
                customerID = object.getString("id");
                Toast.makeText(getContext(), customerID, Toast.LENGTH_SHORT).show();
                getEphericalKey(customerID);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration("VISA", new PaymentSheet.CustomerConfiguration(customerID, EphericalKey))
        );
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "Pago completado 😎", Toast.LENGTH_SHORT).show();
//            navigateToDetailOrder();
        }
    }

    private void navigateToDetailOrder() {
        Fragment fragment = new DetailTicketFragment();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getClientSecret(String customerID, String ephericalKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", response -> {
            try {
                JSONObject object = new JSONObject(response);
                ClientSecret = object.getString("client_secret");
                Toast.makeText(getContext(), ClientSecret, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {

        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;

            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "10" + "00");
                params.put("currency", "PEN");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getEphericalKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", response -> {
            try {
                JSONObject object = new JSONObject(response);
                EphericalKey = object.getString("id");
                Toast.makeText(getContext(), EphericalKey, Toast.LENGTH_SHORT).show();

                getClientSecret(customerID, EphericalKey);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2022-11-15 ");
                return header;

            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, requestCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (paymentConfirmation != null) {
                try {
                    String paymentDetails = paymentConfirmation.toJSONObject().toString();
                    JSONObject object = new JSONObject(paymentDetails);

                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT);
                }

            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Error 😕", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(getContext(), "Pago Invalido 😢", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.bottomNavigationView)
                .show();
    }

}