package com.paqta.paqtafood.screens.detail_dishes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;

import java.util.List;


public class DetailDishesFragment extends Fragment {

    MaterialButton btnAddFavorito, btnAddCart, btnShare;
    String idProd;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    Drawable iconFavoriteFilled, iconFavoriteOutlined;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idProd = getArguments().getString("idProd");
        }
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

        iconFavoriteFilled = ContextCompat.getDrawable(getContext(), R.drawable.baseline_favorite_24);
        iconFavoriteOutlined = ContextCompat.getDrawable(getContext(), R.drawable.baseline_favorite_border_24);

        btnAddFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductoToFavorite(v);
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addProductoToCart(v);
            }

        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Compartir", Snackbar.LENGTH_SHORT).show();
            }
        });

        verificarFavorito();
        return root;
    }

    private void verificarFavorito() {
        DocumentReference usuarioRef = mFirestore.collection("usuarios").document(mUser.getUid());
        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");
                if (favoritos != null && favoritos.contains(idProd)) {
                    // El producto está en la lista de favoritos
                    btnAddFavorito.setText("Quitar de favoritos");
                    btnAddFavorito.setIcon(iconFavoriteFilled);
                } else {
                    // El producto no está en la lista de favoritos
                    btnAddFavorito.setText("Añadir favoritos");
                    btnAddFavorito.setIcon(iconFavoriteOutlined);
                }
            }
        });
    }

    private void addProductoToFavorite(View v) {
        DocumentReference usuarioRef = mFirestore.collection("usuarios").document(mUser.getUid());
        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                if (favoritos != null && favoritos.contains(idProd)) {
                    usuarioRef.update("favoritos", FieldValue.arrayRemove(idProd))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    btnAddFavorito.setText("Añadir favoritos");
                                    btnAddFavorito.setIcon(iconFavoriteOutlined);
                                    Snackbar.make(v, "Eliminado de favoritos", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(v, "Error al eliminar de favoritos", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    usuarioRef.update("favoritos", FieldValue.arrayUnion(idProd))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    btnAddFavorito.setText("Quitar de favoritos");
                                    btnAddFavorito.setIcon(iconFavoriteFilled);

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
            }
        });
    }

    private void addProductoToCart(View v) {
        mFirestore.collection("usuarios").document(mUser.getUid()).update("carrito", FieldValue.arrayUnion(idProd))
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


}