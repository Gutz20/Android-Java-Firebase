package com.paqta.paqtafood.ui.user.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardSearchAdapter;
import com.paqta.paqtafood.model.Producto;

public class SearchFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    RecyclerView mRecycler;
    CardSearchAdapter mAdapter;
    SearchView searchView;
    Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        searchView = root.findViewById(R.id.searchView);
        mRecycler = root.findViewById(R.id.recyclerSearch);

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        setupRecycler(query);
        setupSearchView();
        return root;
    }

    private void setupRecycler(Query query) {
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(layoutManager1);


        FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                .setQuery(query, Producto.class)
                .build();

        mAdapter = new CardSearchAdapter(options, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(newText);
                return false;
            }
        });
    }

    private void textSearch(String s) {
        String busqueda = s.toLowerCase();
        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query.whereGreaterThanOrEqualTo("searchField", busqueda)
                                .whereLessThanOrEqualTo("searchField", busqueda + "\uf8ff"), Producto.class).build();

        mAdapter = new CardSearchAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
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