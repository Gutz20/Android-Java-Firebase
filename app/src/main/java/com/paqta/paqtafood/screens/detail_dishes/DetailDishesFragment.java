package com.paqta.paqtafood.screens.detail_dishes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;


public class DetailDishesFragment extends Fragment {

    Button btnAddFavorito, btnAddCart, btnShare;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_dishes, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnAddFavorito = root.findViewById(R.id.btnAddFavorito);
        btnAddCart = root.findViewById(R.id.btnAddCart);
        btnShare = root.findViewById(R.id.btnShare);

        btnAddFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prueba = "321";

                mFirestore.collection("usuarios").document(mUser.getUid()).update("favoritos", FieldValue.arrayUnion(prueba))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(v, "Añadido a favoritos", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(v, "Error al añadir a favoritos", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prueba = "123";
                mFirestore.collection("usuarios").document(mUser.getUid()).update("carrito", FieldValue.arrayUnion(prueba))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(v, "Se añadio a tu carrito", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(v, "Eliminado de tu carrito", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Compartir", Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}