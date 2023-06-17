package com.paqta.paqtafood.ui.user.cart.components;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.paqta.paqtafood.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondStepCartFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    MaterialButton btnGetLocation, btnOpenMap;
    TextInputEditText editTextUbication;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int PLACE_AUTOCOMPLETE_REQUEST = 1;
    private static final int RESULT_OK = -1;

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

        btnOpenMap = view.findViewById(R.id.btnOpenMap);
        editTextUbication = view.findViewById(R.id.textUbicationSelected);

        // GOOGLE MAPS
        permisoUbicacion();

        btnOpenMap.setOnClickListener(v -> startPlacePicker());
    }

    private void startPlacePicker() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getActivity());
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST);
    }

    private void permisoUbicacion() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void obtenerDuracionViaje(LatLng origen, LatLng destino, TravelMode travelMode) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_api_key))
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(geoApiContext)
                .origin(origen.latitude + "," + origen.longitude)
                .destination(destino.latitude + "," + destino.longitude)
                .mode(travelMode);

        try {
            DirectionsResult result = request.await();
            if (result.routes != null && result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];

                long duracionSegundos = route.legs[0].duration.inSeconds;
                String duracionTexto = route.legs[0].duration.humanReadable;

                Log.d("mapa", "Duracion del viaje: " + duracionTexto);

                List<LatLng> polylinePoints = new ArrayList<>();
                for (DirectionsLeg leg : route.legs) {
                    for (DirectionsStep step : leg.steps) {
                        EncodedPolyline encodedPolyline = step.polyline;
                        List<com.google.maps.model.LatLng> decodedPolyline = encodedPolyline.decodePath();

                        for (com.google.maps.model.LatLng latLng : decodedPolyline) {
                            polylinePoints.add(new LatLng(latLng.lat, latLng.lng));
                        }
                    }
                }

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(polylinePoints);
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(5);

                mMap.addPolyline(polylineOptions);
                mostrarDuracionEnMapa(duracionTexto, polylinePoints);
            }

        } catch (com.google.maps.errors.ApiException | InterruptedException | IOException e) {
            Log.d("mapa", e.getMessage());
        }
    }



    private void mostrarDuracionEnMapa(String duracionTexto, List<LatLng> polylinePoints) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : polylinePoints) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        LatLng center = bounds.getCenter();

        double distance = calculateDistance(bounds.southwest, bounds.northeast);
        int size = (int) (distance / 6);

        TextView textView = new TextView(getContext());
        textView.setText(duracionTexto);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(8, 4, 8, 4);
        textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(textView.getMeasuredWidth(), textView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.draw(canvas);

        LatLng newPosition = new LatLng(center.latitude, center.longitude + 0.002); // Ajusta la posición en longitud

        GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(newPosition, size);

        mMap.addGroundOverlay(overlayOptions);
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        double lat1 = Math.toRadians(point1.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon2 = Math.toRadians(point2.longitude);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radio de la Tierra en metros
        double radius = 6371000;

        return radius * c;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Agregar los marcadores iniciales (locales) al mapa y a la lista
        LatLng localLatLng1 = new LatLng(-14.0670, -75.7257);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Obtiene el lugar seleccionado
                Place place = Autocomplete.getPlaceFromIntent(data);

                // Aquí puedes obtener los detalles del lugar seleccionado
                String placeName = place.getName();
                LatLng placeLatLng = place.getLatLng();

                editTextUbication.setText(placeName);
                Log.d("mapa", placeName.toString());
                Log.d("mapa", placeLatLng.toString());

                mMap.clear();

                obtenerDuracionViaje(localLatLng1, placeLatLng, TravelMode.DRIVING);

                // Marcador del local (?)
                mMap.addMarker(new MarkerOptions().position(localLatLng1).title("Ica"));

                mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(placeLatLng)
                        .zoom(12)
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Ocurrió un error al seleccionar el lugar
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("mapa", status.getStatusMessage());
            }
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
//        mMap.setTrafficEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están concedidos, solicitarlos al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        LatLng ica = new LatLng(-14.0670, -75.7257);
        mMap.addMarker(new MarkerOptions().position(ica).title("Ica"));

        // Configurar la cámara para mostrar los marcadores
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ica) // Ubicación de la cámara
                .zoom(12) // Nivel de zoom
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}