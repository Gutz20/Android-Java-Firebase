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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.adapters.CardFavoriteAdapterV2;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.model.User;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    RecyclerView mRecyclerPlatillos, mRecyclerBebibas, mRecyclerPostres;
    CardFavoriteAdapterV2 mAdapterPlatillos, mAdapterBebidas, mAdapterPostres;

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

        mFirestore.collection("usuarios")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoritos = documentSnapshot.toObject(User.class).getFavoritos();

                        // Verificar si la lista de favoritos no es nula o vacía
                        if (favoritos != null && !favoritos.isEmpty()) {
                            // Obtener los productos favoritos de Firestore
                            FirebaseFirestore.getInstance().collection("productos")
                                    .whereIn(FieldPath.documentId(), favoritos)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        List<Producto> productosList = new ArrayList<>();
                                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Producto producto = snapshot.toObject(Producto.class);
                                            productosList.add(producto);
                                        }

                                        // Llama a un método para configurar el RecyclerView con los datos
                                        setupRecycler(productosList);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Maneja el error de recuperación de datos de Firestore
                                    });
                        } else {
                            // La lista de favoritos está vacía
                            // Llama a un método para configurar el RecyclerView con una lista vacía
                            setupRecycler(new ArrayList<>());
                        }
                    } else {
                        // El documento de usuario no existe
                        // Llama a un método para configurar el RecyclerView con una lista vacía
                        setupRecycler(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja el error de recuperación de datos de Firestore
                });
    }

    private void setupRecycler(List<Producto> productosList) {
        mAdapterPlatillos = new CardFavoriteAdapterV2(productosList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerPlatillos.setLayoutManager(layoutManager);
        mRecyclerPlatillos.setAdapter(mAdapterPlatillos);
    }


}