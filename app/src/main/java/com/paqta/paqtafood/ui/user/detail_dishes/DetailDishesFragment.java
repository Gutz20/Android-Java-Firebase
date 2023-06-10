package com.paqta.paqtafood.ui.user.detail_dishes;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
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


public class DetailDishesFragment extends Fragment {

    MaterialButton btnAddFavorito, btnAddCart, btnShare;
    String idProd;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Drawable iconFavoriteFilled, iconFavoriteOutlined, iconAddToCart, iconRemoveFromCart;
    DocumentReference usuarioRef;
    ImageView imageProduct;
    TextView titleTextView, textViewDetalles;

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
        iconAddToCart = ContextCompat.getDrawable(getContext(), R.drawable.baseline_add_shopping_cart_24);
        iconRemoveFromCart = ContextCompat.getDrawable(getContext(), R.drawable.baseline_remove_shopping_cart_24);

        titleTextView = root.findViewById(R.id.titleTextDetail);
        imageProduct = root.findViewById(R.id.imgPlatillo);
        textViewDetalles = root.findViewById(R.id.textViewDetalles);


        usuarioRef = mFirestore.collection("usuarios").document(mUser.getUid());


        btnAddFavorito.setOnClickListener(this::addProductoToFavorite);
        btnAddCart.setOnClickListener(this::addProductoToCart);
        btnShare.setOnClickListener(this::configurarCompartir);

        getProductById();
        verificarEstados();
        return root;
    }

    private void getProductById() {
        mFirestore.collection("productos").document(idProd).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombre = documentSnapshot.getString("nombre");
                        String imagen = documentSnapshot.getString("imagen");

                        List<String> detalles = new ArrayList<>();
                        if (documentSnapshot.contains("detalles")) {
                            List<Object> detallesObjects = (List<Object>) documentSnapshot.get("detalles");
                            for (Object detalleObject : detallesObjects) {
                                detalles.add(detalleObject.toString());
                            }
                        }
                        String detallesText = TextUtils.join("\n", detalles);

                        titleTextView.setText(nombre);
                        Glide.with(getView()).load(imagen).into(imageProduct);
                        textViewDetalles.setText(detallesText);
                    }
                });
    }

    private void configurarCompartir(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottomsheetlayout);

        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);

        // Muestra el Bottom Sheet
        bottomSheetDialog.show();
    }

    private void verificarEstados() {
        usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> carrito = (List<String>) documentSnapshot.get("carrito");
                List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                if (carrito != null && carrito.contains(idProd)) {
                    // El producto está en el carrito
                    btnAddCart.setText("Quitar del carrito");
                    btnAddCart.setIcon(iconRemoveFromCart);
                } else {
                    // El producto no está en el carrito
                    btnAddCart.setText("Agregar carrito");
                    btnAddCart.setIcon(iconAddToCart);
                }

                if (favoritos != null && favoritos.contains(idProd)) {
                    // El producto está en favoritos
                    btnAddFavorito.setText("Quitar favoritos");
                    btnAddFavorito.setIcon(iconFavoriteFilled);
                } else {
                    // El producto no está en favoritos
                    btnAddFavorito.setText("Añadir favoritos");
                    btnAddFavorito.setIcon(iconFavoriteOutlined);
                }
            }
        });
    }

    private void addProductoToFavorite(View v) {
        usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");
            boolean isInFavorites = favoritos != null && favoritos.contains(idProd);

            if (isInFavorites) {
                actualizarLista("favoritos", idProd, false, v, "Eliminado de favoritos", "Error al eliminar");
            } else {
                actualizarLista("favoritos", idProd, true, v, "Añadido a favoritos", "Error al añadir a favoritos");
            }

            actualizarFavoriteBtn(!isInFavorites);
        });


    }

    private void addProductoToCart(View v) {
        usuarioRef.get().addOnSuccessListener(documentSnapshot -> {
            List<String> carrito = (List<String>) documentSnapshot.get("carrito");
            boolean isInCart = carrito != null && carrito.contains(idProd);

            if (isInCart) {
                actualizarLista("carrito", idProd, false, v, "Producto quitado del carrito", "Error al quitar el producto del carrito");
            } else {
                actualizarLista("carrito", idProd, true, v, "Producto añadido al carrito", "Error al añadir el producto al carrito");
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
        btnAddFavorito.setText(isInFavorite ? "Quitar Favorito" : "Añadir Favorito");
        btnAddFavorito.setIcon(isInFavorite ? iconFavoriteFilled : iconFavoriteOutlined);
    }

    private void actualizarCartBtn(boolean isInCart) {
        btnAddCart.setText(isInCart ? "Remover Carrito" : "Añadir Carrito");
        btnAddCart.setIcon(isInCart ? iconRemoveFromCart : iconAddToCart);
    }


}