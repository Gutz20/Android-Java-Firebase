package com.paqta.paqtafood.ui.user.favorite;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.model.User;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    RecyclerView mRecyclerPlatillos, mRecyclerBebibas, mRecyclerPostres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mRecyclerPlatillos = view.findViewById(R.id.favoritesPlatillos);
        mRecyclerBebibas = view.findViewById(R.id.favoritesBebidas);
        mRecyclerPostres = view.findViewById(R.id.favoritesPostres);

        obtenerProductosFavoritos("Platillos",
                platillos -> setupRecycler(platillos, mRecyclerPlatillos),
                Throwable::printStackTrace
        );


        obtenerProductosFavoritos("Bebidas",
                bebidas -> setupRecycler(bebidas, mRecyclerBebibas),
                Throwable::printStackTrace
        );

        obtenerProductosFavoritos("Postres",
                postres -> setupRecycler(postres, mRecyclerPostres),
                Throwable::printStackTrace
        );
    }

    private void setupRecycler(List<Producto> productosList, RecyclerView recyclerView) {
        CardFavoriteAdapter adapter = new CardFavoriteAdapter(productosList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void obtenerProductosFavoritos(String categoria, OnSuccessListener<List<Producto>> successListener, OnFailureListener failureListener) {
        mFirestore.collection("usuarios")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoritos = documentSnapshot.toObject(User.class).getFavoritos();

                        // Verificar si la lista de favoritos no es nula o vacía
                        if (favoritos != null && !favoritos.isEmpty()) {
                            mFirestore.collection("productos")
                                    .whereIn(FieldPath.documentId(), favoritos)
                                    .whereEqualTo("categoria", categoria)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        List<Producto> productosList = new ArrayList<>();
                                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Producto producto = snapshot.toObject(Producto.class);
                                            productosList.add(producto);
                                        }

                                        successListener.onSuccess(productosList);
                                    })
                                    .addOnFailureListener(failureListener);
                        } else {
                            // La lista de favoritos está vacía
                            successListener.onSuccess(new ArrayList<>());
                        }
                    } else {
                        // El documento de usuario no existe
                        successListener.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(failureListener);
    }


}