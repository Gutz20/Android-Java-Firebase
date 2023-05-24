package com.paqta.paqtafood.screens.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.adapters.CardSearchAdapter;
import com.paqta.paqtafood.adapters.PlatilloAdapter;
import com.paqta.paqtafood.model.Platillo;

import java.util.List;

public class SearchFragment extends Fragment {


    private FirebaseFirestore mFirestore;
    RecyclerView mRecycler, mRecyclerOtherMenu;
    CardSearchAdapter mAdapter;
    CardFavoriteAdapter mAdapterOtherMenu;
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
        mRecyclerOtherMenu = root.findViewById(R.id.recyclerOtherMenus);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);

        mRecycler.setLayoutManager(layoutManager1);
        mRecyclerOtherMenu.setLayoutManager(layoutManager2);

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Platillo> options = new FirestoreRecyclerOptions.Builder<Platillo>()
                .setQuery(query, Platillo.class)
                .build();

        mAdapter = new CardSearchAdapter(options, getActivity(), getActivity().getSupportFragmentManager());
        mAdapterOtherMenu = new CardFavoriteAdapter(options);
        mAdapter.notifyDataSetChanged();
        mAdapterOtherMenu.notifyDataSetChanged();

        mRecycler.setAdapter(mAdapter);
        mRecyclerOtherMenu.setAdapter(mAdapterOtherMenu);

        setupSearchView();
        return root;
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
        FirestoreRecyclerOptions<Platillo> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Platillo>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s).endAt(s + "~"), Platillo.class).build();
        mAdapter = new CardSearchAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        mAdapterOtherMenu.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
        mAdapterOtherMenu.stopListening();
    }
}