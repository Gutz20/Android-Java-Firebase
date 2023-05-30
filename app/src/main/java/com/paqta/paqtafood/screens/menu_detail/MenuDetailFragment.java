package com.paqta.paqtafood.screens.menu_detail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardMenuDetailAdapter;
import com.paqta.paqtafood.model.Producto;


public class MenuDetailFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;
    CardMenuDetailAdapter menuDetailAdapter;
    Query query;
    private Integer NUMBER_OF_COLUMNS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_detail, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        recyclerView = root.findViewById(R.id.recyclerMenuDetail);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        recyclerView.setLayoutManager(gridLayoutManager);

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query, Producto.class)
                        .build();

        menuDetailAdapter = new CardMenuDetailAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        menuDetailAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(menuDetailAdapter);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        menuDetailAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        menuDetailAdapter.stopListening();
    }
}