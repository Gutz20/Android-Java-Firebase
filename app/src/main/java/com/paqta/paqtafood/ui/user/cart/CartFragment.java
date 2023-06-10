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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.shuhart.stepview.StepView;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class CartFragment extends Fragment implements OnMapReadyCallback {
    StepView stepView;
    TextView stepTextView;
    MaterialButton btnGetLocation;
    Button btnReservar, btnComprar;
    RecyclerView rycrCart;
    GoogleMap mMap;
    CardCartAdapter mAdapterCart;
    LinearLayout layoutContaint;

    int stepIndex = 0;
    String[] stepsTexts = {"CARRITO", "ENTREGA", "METODO DE PAGO"};
    List<TextView> textViewList = new ArrayList<>();

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FusedLocationProviderClient fusedLocationClient;

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

        btnReservar = view.findViewById(R.id.btnReservar);
        btnComprar = view.findViewById(R.id.btnComprar);
        btnGetLocation = view.findViewById(R.id.btnGetUbication);

        rycrCart = view.findViewById(R.id.cartPlatillos);

        layoutContaint = view.findViewById(R.id.linearLayoutContaint);

        stepTextView = view.findViewById(R.id.stepTextView);
        stepView = view.findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        btnComprar.setOnClickListener(v -> configureStep());

        // GOOGLE MAPS
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        btnGetLocation.setOnClickListener(v -> {
//            ActivityResultLauncher<String []> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
//                    .RequestMultiplePermissions(), result -> {
//                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
//                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
//                if (fineLocationGranted != null && fineLocationGranted) {
//
//                } else if (coarseLocationGranted != null && coarseLocationGranted) {
//
//                } else {
//
//                }
//            });
//
//            locationPermissionRequest.launch(new String[] {
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            });
//        });

        configureRecyclers();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterCart != null) {
            mAdapterCart.stopListening();
        }
    }

    private void configureRecyclers() {
        DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> carrito = (List<String>) documentSnapshot.get("carrito");

                if (carrito != null && !carrito.isEmpty()) {
                    rycrCart.setLayoutManager(new LinearLayoutManager(getContext()));

                    Query queryPlatillos = mFirestore.collection("productos")
                            .whereIn("id", carrito);

                    FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(queryPlatillos, Producto.class)
                            .build();

                    mAdapterCart = new CardCartAdapter(options, getActivity(), getActivity().getSupportFragmentManager());
                    mAdapterCart.notifyDataSetChanged();
                    rycrCart.setAdapter(mAdapterCart);
                    mAdapterCart.startListening();
                }
            }
        });
    }


    private void configureStep() {
        stepIndex++;
        if (stepIndex < stepsTexts.length) {
            stepTextView.setText(stepsTexts[stepIndex]);
            stepView.go(stepIndex, true);
        }
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




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        LatLng mexico = new LatLng(19.8077463, -99.4077038);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Mexico"));

        // Configurar la cámara para mostrar los marcadores
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mexico) // Ubicación de la cámara
                .zoom(12) // Nivel de zoom
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



}