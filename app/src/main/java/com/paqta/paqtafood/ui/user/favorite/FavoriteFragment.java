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
import com.paqta.paqtafood.model.Producto;

import java.util.List;


public class FavoriteFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    RecyclerView mRecyclerPlatillos, mRecyclerBebibas, mRecyclerPostres;
    CardFavoriteAdapter mAdapterPlatillos, mAdapterBebidas, mAdapterPostres;

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

        setupRecycler();
    }

    private void setupRecycler() {
        DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> favoritos = (List<String>) value.get("favoritos");

                if (favoritos != null && !favoritos.isEmpty()) {
                    Query query = mFirestore.collection("productos")
                            .whereIn(FieldPath.documentId(), favoritos);

                    FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(query, Producto.class)
                            .build();

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                    mRecyclerPlatillos.setLayoutManager(linearLayoutManager);

                    mAdapterPlatillos = new CardFavoriteAdapter(options, getActivity());
                    mAdapterPlatillos.notifyDataSetChanged();
                    mRecyclerPlatillos.setAdapter(mAdapterPlatillos);
                    mAdapterPlatillos.startListening();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapterPlatillos != null) {
            mAdapterPlatillos.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterPlatillos != null) {
            mAdapterPlatillos.stopListening();
        }
    }

}