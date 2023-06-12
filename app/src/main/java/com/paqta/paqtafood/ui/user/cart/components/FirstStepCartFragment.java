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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardCartAdapter;
import com.paqta.paqtafood.model.Producto;

import java.util.List;


public class FirstStepCartFragment extends Fragment {

    RecyclerView recyclerProductsCart;
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

        recyclerProductsCart = view.findViewById(R.id.cartProductos);

        configureRecyclers();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    private void configureRecyclers() {
        DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> carrito = (List<String>) documentSnapshot.get("carrito");

                if (carrito != null && !carrito.isEmpty()) {
                    recyclerProductsCart.setLayoutManager(new LinearLayoutManager(getContext()));

                    Query queryPlatillos = mFirestore.collection("productos")
                            .whereIn("id", carrito);

                    FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(queryPlatillos, Producto.class)
                            .build();

                    mAdapter = new CardCartAdapter(options, getActivity(), getActivity().getSupportFragmentManager());
                    mAdapter.notifyDataSetChanged();
                    recyclerProductsCart.setAdapter(mAdapter);
                    mAdapter.startListening();
                }
            }
        });
    }
}