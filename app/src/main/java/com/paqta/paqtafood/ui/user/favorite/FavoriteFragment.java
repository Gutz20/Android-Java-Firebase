package com.paqta.paqtafood.ui.user.favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mRecyclerPlatillos = root.findViewById(R.id.favoritesPlatillos);
        mRecyclerBebibas = root.findViewById(R.id.favoritesBebidas);
        mRecyclerPostres = root.findViewById(R.id.favoritesPostres);

        configurarRecyclers();
        return root;
    }

    private void configurarRecyclers() {
        DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> favoritos = (List<String>) documentSnapshot.get("favoritos");

                if (favoritos != null && !favoritos.isEmpty()) {
                    // Categoría 1
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    mRecyclerPlatillos.setLayoutManager(linearLayoutManager);

                    Query queryCategoria1 = mFirestore.collection("productos")
                            .whereIn("id", favoritos)
                            .whereEqualTo("categoria", "Platillos");

                    FirestoreRecyclerOptions<Producto> optionsCategoria1 = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(queryCategoria1, Producto.class)
                            .build();
                    mAdapterPlatillos = new CardFavoriteAdapter(optionsCategoria1, favoritos);
                    mAdapterPlatillos.notifyDataSetChanged();
                    mRecyclerPlatillos.setAdapter(mAdapterPlatillos);
                    mAdapterPlatillos.startListening();

                    // Categoría 2 (Bebidas)
//                    LinearLayoutManager linearLayoutManagerBebidas = new LinearLayoutManager(getContext());
//                    linearLayoutManagerBebidas.setOrientation(RecyclerView.VERTICAL);
//                    mRecyclerBebibas.setLayoutManager(linearLayoutManagerBebidas);
//                    Query queryCategoriaBebidas = mFirestore.collection("productos")
//                            .whereIn("id", favoritos)
//                            .whereEqualTo("categoria", "Bebidas");
//                    FirestoreRecyclerOptions<Producto> optionsCategoriaBebidas = new FirestoreRecyclerOptions.Builder<Producto>()
//                            .setQuery(queryCategoriaBebidas, Producto.class)
//                            .build();
//                    mAdapterBebidas = new CardFavoriteAdapter(optionsCategoriaBebidas, favoritos);
//                    mAdapterBebidas.notifyDataSetChanged();
//                    mRecyclerBebibas.setAdapter(mAdapterBebidas);
//                    mAdapterBebidas.startListening();
//
//                    // Categoría 3 (Postres)
//                    LinearLayoutManager linearLayoutManagerPostres = new LinearLayoutManager(getContext());
//                    linearLayoutManagerPostres.setOrientation(RecyclerView.VERTICAL);
//                    mRecyclerPostres.setLayoutManager(linearLayoutManagerPostres);
//                    Query queryCategoriaPostres = mFirestore.collection("productos")
//                            .whereIn("id", favoritos)
//                            .whereEqualTo("categoria", "Postres");
//                    FirestoreRecyclerOptions<Producto> optionsCategoriaPostres = new FirestoreRecyclerOptions.Builder<Producto>()
//                            .setQuery(queryCategoriaPostres, Producto.class)
//                            .build();
//                    mAdapterPostres = new CardFavoriteAdapter(optionsCategoriaPostres, favoritos);
//                    mAdapterPostres.notifyDataSetChanged();
//                    mRecyclerPostres.setAdapter(mAdapterPostres);
//                    mAdapterPostres.startListening();
                } else {
                    Toast.makeText(getContext(), "Aun no tienes nada en favoritos 😔", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterPlatillos != null && mAdapterBebidas != null && mAdapterPostres != null) {
            mAdapterPlatillos.stopListening();
            mAdapterBebidas.stopListening();
            mAdapterPostres.stopListening();
        }

    }

}