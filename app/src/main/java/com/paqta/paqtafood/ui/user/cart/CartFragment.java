package com.paqta.paqtafood.ui.user.cart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardCartAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.cart.components.FirstStepCartFragment;
import com.paqta.paqtafood.ui.user.cart.components.SecondStepCartFragment;
import com.paqta.paqtafood.ui.user.cart.components.ThirdStepCartFragment;
import com.shuhart.stepview.StepView;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


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