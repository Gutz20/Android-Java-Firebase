package com.paqta.paqtafood.ui.user.cart.components;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.paqta.paqtafood.R;

public class SecondStepCartFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    MaterialButton btnGetLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_LOCATION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_step_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGetLocation = view.findViewById(R.id.btnGetUbication);
        btnGetLocation.setOnClickListener(v -> obtenerUbicacionActual());

        // GOOGLE MAPS
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        permisoUbicacion();
    }

    private void permisoUbicacion() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, puedes realizar acciones adicionales aquí

                // Obtener la ubicación actual del usuario
                obtenerUbicacionActual();
            } else {
                // Permiso de ubicación denegado, muestra un mensaje de error o realiza acciones alternativas
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Ubicación actual obtenida con éxito
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng currentLocation = new LatLng(latitude, longitude);

                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Mi ubicación"));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(currentLocation)
                                    .zoom(12)
                                    .build();

                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            Toast.makeText(getContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están concedidos, solicitarlos al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        LatLng mexico = new LatLng(-14.0670, -75.7257);
        mMap.addMarker(new MarkerOptions().position(mexico).title("Ica"));

        // Configurar la cámara para mostrar los marcadores
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mexico) // Ubicación de la cámara
                .zoom(12) // Nivel de zoom
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}