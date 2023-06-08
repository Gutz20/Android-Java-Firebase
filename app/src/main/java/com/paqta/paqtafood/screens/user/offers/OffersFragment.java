package com.paqta.paqtafood.screens.user.offers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardOffersAdapter;
import com.paqta.paqtafood.model.Producto;

public class OffersFragment extends Fragment {


    RecyclerView mRecycler;
    CardOffersAdapter mAdapter;
    FirebaseFirestore mFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offers, container, false);
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = root.findViewById(R.id.recyclerViewOffers);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = mFirestore.collection("productos")
                .whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query, Producto.class).build();

        mAdapter = new CardOffersAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}