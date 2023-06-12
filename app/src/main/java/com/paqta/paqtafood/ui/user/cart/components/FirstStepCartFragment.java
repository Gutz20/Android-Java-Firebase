package com.paqta.paqtafood.ui.user.cart.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardCartAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.model.User;

import java.util.ArrayList;
import java.util.List;


public class FirstStepCartFragment extends Fragment {

    RecyclerView mRecycler;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    CardCartAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_step_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mRecycler = view.findViewById(R.id.cartProductos);

        mFirestore.collection("usuarios")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> carrito = documentSnapshot.toObject(User.class).getCarrito();

                        if (carrito != null && !carrito.isEmpty()) {

                            mFirestore.collection("productos")
                                    .whereIn(FieldPath.documentId(), carrito)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        List<Producto> productoList = new ArrayList<>();
                                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Producto producto = snapshot.toObject(Producto.class);
                                            productoList.add(producto);
                                        }
                                        setupRecycler(productoList);
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            setupRecycler(new ArrayList<>());
                            Toast.makeText(getActivity(), "No tienes nada en tu carrito 😔", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setupRecycler(new ArrayList<>());
                    }
                });
    }

    private void setupRecycler(List<Producto> productoList) {
        mAdapter = new CardCartAdapter(productoList, getActivity().getSupportFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(mAdapter);
    }

}