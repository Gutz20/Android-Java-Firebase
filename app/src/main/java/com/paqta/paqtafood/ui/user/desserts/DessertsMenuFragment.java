package com.paqta.paqtafood.ui.user.desserts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardDrinksAdapter;
import com.paqta.paqtafood.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class DessertsMenuFragment extends Fragment {


    RecyclerView rycDesserts;
    CardDrinksAdapter mAdapter;
    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_desserts_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rycDesserts = view.findViewById(R.id.rycDesserts);
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("productos")
                .whereEqualTo("categoria", "Postres")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        List<Producto> productoList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot: value){
                            Producto producto = documentSnapshot.toObject(Producto.class);
                            productoList.add(producto);
                        }
                        setupRecycler(productoList);
                    }
                });
    }

    private void setupRecycler(List<Producto> productoList) {
        mAdapter = new CardDrinksAdapter(productoList, getActivity().getSupportFragmentManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rycDesserts.setLayoutManager(linearLayoutManager);
        rycDesserts.setAdapter(mAdapter);
    }
}