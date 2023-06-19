package com.paqta.paqtafood.ui.user.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardCartAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.cart.components.FirstStepCartFragment;
import com.paqta.paqtafood.ui.user.cart.components.SecondStepCartFragment;
import com.paqta.paqtafood.ui.user.cart.components.ThirdStepCartFragment;
import com.shuhart.stepview.StepView;

import java.util.List;


public class CartFragment extends Fragment {
    StepView stepView;
    TextView stepTextView;
    Button btnComprar;
    int stepIndex = 0;
    String[] stepsTexts = {"CARRITO", "ENTREGA", "METODO DE PAGO"};
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FrameLayout frameContainer;
    LinearLayout layoutDataCart;
    ScrollView scrollView;
    TextView textViewTotal, textViewSubtotal;

    CardCartAdapter cardCartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnComprar = view.findViewById(R.id.btnComprar);
        frameContainer = view.findViewById(R.id.fragmentContainer);
        layoutDataCart = view.findViewById(R.id.layoutDataCart);
        scrollView = view.findViewById(R.id.scrollViewCart);

        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewSubtotal = view.findViewById(R.id.textViewSubTotal);

        stepTextView = view.findViewById(R.id.stepTextView);
        stepView = view.findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        btnComprar.setOnClickListener(v -> configureStep());
        replaceFragment(new FirstStepCartFragment());
    }



    private void configureStep() {
        stepIndex++;
        if (stepIndex < stepsTexts.length) {
            stepTextView.setText(stepsTexts[stepIndex]);
            stepView.go(stepIndex, true);

            switch (stepIndex) {
                case 0:
                    replaceFragment(new FirstStepCartFragment());
                    break;
                case 1:
                    replaceFragment(new SecondStepCartFragment());
                    break;
                case 2:
                    replaceFragment(new ThirdStepCartFragment());
                    break;
            }

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}