package com.paqta.paqtafood.screens.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.adapters.CardSearchAdapter;
import com.paqta.paqtafood.model.Platillo;

import java.util.List;

public class SearchFragment extends Fragment {


    private FirebaseFirestore mFirestore;
    RecyclerView mRecycler, mRecyclerOtherMenu;
    CardSearchAdapter mAdapter;
    CardFavoriteAdapter mAdapterOtherMenu;

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
        mRecycler = root.findViewById(R.id.recyclerSearch);
        mRecyclerOtherMenu = root.findViewById(R.id.recyclerOtherMenus);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);

        mRecycler.setLayoutManager(layoutManager1);
        mRecyclerOtherMenu.setLayoutManager(layoutManager2);

        query = mFirestore.collection("platillos");

        FirestoreRecyclerOptions<Platillo> options = new FirestoreRecyclerOptions.Builder<Platillo>()
                .setQuery(query, Platillo.class)
                .build();

        mAdapter = new CardSearchAdapter(options);
        mAdapterOtherMenu = new CardFavoriteAdapter(options);
        mAdapter.notifyDataSetChanged();
        mAdapterOtherMenu.notifyDataSetChanged();

        mRecycler.setAdapter(mAdapter);
        mRecyclerOtherMenu.setAdapter(mAdapterOtherMenu);

        return root;
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