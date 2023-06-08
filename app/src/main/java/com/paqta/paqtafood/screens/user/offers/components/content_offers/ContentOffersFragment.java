package com.paqta.paqtafood.screens.user.offers.components.content_offers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.List;

public class ContentOffersFragment extends Fragment {

    String idOffer;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    DocumentReference usuarioRef;
    TextView textDetailOffer;
    ImageView imageViewOffer;
    MaterialButton btnFavorito, btnCart;
    Drawable iconFavoriteFilled, iconFavoriteOutlined, iconAddToCart, iconRemoveFromCart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idOffer = getArguments().getString("idOffer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_content_offers, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        textDetailOffer = root.findViewById(R.id.textViewDetailOffer);
        imageViewOffer = root.findViewById(R.id.imgOfferDetail);

        btnFavorito = root.findViewById(R.id.btnAddFavorito);
        btnCart = root.findViewById(R.id.btnAddCart);

        iconFavoriteFilled = ContextCompat.getDrawable(getContext(), R.drawable.baseline_favorite_24);
        iconFavoriteOutlined = ContextCompat.getDrawable(getContext(), R.drawable.baseline_favorite_border_24);
        iconAddToCart = ContextCompat.getDrawable(getContext(), R.drawable.baseline_add_shopping_cart_24);
        iconRemoveFromCart = ContextCompat.getDrawable(getContext(), R.drawable.baseline_remove_shopping_cart_24);

        usuarioRef = mFirestore.collection("usuarios").document(mUser.getUid());

        btnFavorito.setOnClickListener(this::addProductoToFavorite);
        btnCart.setOnClickListener(this::addProductoToCart);

        getProductById();
        verificarEstados();
        return root;
    }

    private void verificarEstados() {
        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> carrito = (List<String>) documentSnapshot.get("carrito");
                List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                if (carrito != null && carrito.contains(idOffer)) {
                    // El producto está en el carrito
                    btnCart.setText("Quitar del carrito");
                    btnCart.setIcon(iconRemoveFromCart);
                } else {
                    // El producto no está en el carrito
                    btnCart.setText("Agregar carrito");
                    btnCart.setIcon(iconAddToCart);
                }

                if (favoritos != null && favoritos.contains(idOffer)) {
                    // El producto está en favoritos
                    btnFavorito.setText("Quitar favoritos");
                    btnFavorito.setIcon(iconFavoriteFilled);
                } else {
                    // El producto no está en favoritos
                    btnFavorito.setText("Añadir favoritos");
                    btnFavorito.setIcon(iconFavoriteOutlined);
                }
            }
        });
    }

    private void getProductById() {
        mFirestore.collection("productos").document(idOffer).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        String imagen = documentSnapshot.getString("imagen");

                        List<String> detalles = new ArrayList<>();
                        if (documentSnapshot.contains("detalles")) {
                            List<Object> detallesObjects = (List<Object>) documentSnapshot.get("detalles");
                            for (Object detalleObject : detallesObjects) {
                                detalles.add(detalleObject.toString());
                            }
                        }
                        String detallesText = TextUtils.join("\n", detalles);

//                        Glide.with(getView()).load(imagen).into(imageViewOffer);
                        textDetailOffer.setText(detallesText);
                    }
                });
    }

    private void addProductoToFavorite(View v) {
        usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");
            boolean isInFavorites = favoritos != null && favoritos.contains(idOffer);

            if (isInFavorites) {
                actualizarLista("favoritos", idOffer, false, v, "Eliminado de favoritos", "Error al eliminar");
            } else {
                actualizarLista("favoritos", idOffer, true, v, "Añadido a favoritos", "Error al añadir a favoritos");
            }

            actualizarFavoriteBtn(!isInFavorites);
        });


    }

    private void addProductoToCart(View v) {
        usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> carrito = (List<String>) documentSnapshot.get("carrito");
            boolean isInCart = carrito != null && carrito.contains(idOffer);

            if (isInCart) {
                actualizarLista("carrito", idOffer, false, v, "Producto quitado del carrito", "Error al quitar el producto del carrito");
            } else {
                actualizarLista("carrito", idOffer, true, v, "Producto añadido al carrito", "Error al añadir el producto al carrito");
            }
            actualizarCartBtn(!isInCart);
        });
    }

    private void actualizarLista(String lista, String elemento, boolean agregar, View v, String mensajeExito, String mensajeError) {
        if (agregar) {
            usuarioRef.update(lista, FieldValue.arrayUnion(elemento))
                    .addOnSuccessListener(unused -> {
                        mostrarMensaje(v, mensajeExito);
                    })
                    .addOnFailureListener(e -> {
                        mostrarMensaje(v, mensajeError);
                    });
        } else {
            usuarioRef.update(lista, FieldValue.arrayRemove(elemento))
                    .addOnSuccessListener(unused -> {
                        mostrarMensaje(v, mensajeExito);
                    })
                    .addOnFailureListener(e -> {
                        mostrarMensaje(v, mensajeError);
                    });
        }
    }

    private void mostrarMensaje(View v, String mensaje) {
        Snackbar.make(v, mensaje, Snackbar.LENGTH_SHORT).show();
    }

    private void actualizarFavoriteBtn(boolean isInFavorite) {
        btnFavorito.setText(isInFavorite ? "Quitar Favorito" : "Añadir Favorito");
        btnFavorito.setIcon(isInFavorite ? iconFavoriteFilled : iconFavoriteOutlined);
    }

    private void actualizarCartBtn(boolean isInCart) {
        btnCart.setText(isInCart ? "Remover Carrito" : "Añadir Carrito");
        btnCart.setIcon(isInCart ? iconRemoveFromCart : iconAddToCart);
    }

}