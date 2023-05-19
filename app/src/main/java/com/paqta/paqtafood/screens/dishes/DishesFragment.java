package com.paqta.paqtafood.screens.dishes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.PlatilloAdapter;
import com.paqta.paqtafood.model.Platillo;
import com.paqta.paqtafood.screens.dishes.components.FormDishesFragment;


public class DishesFragment extends Fragment {

    FloatingActionButton fab;
    Button btnAdd;
    RecyclerView mRecycler;
    PlatilloAdapter mAdapter;
    FirebaseFirestore mFirestore;
    SearchView searchView;
    Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dishes, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        searchView = root.findViewById(R.id.searchPlatillo);
        btnAdd = root.findViewById(R.id.btnAddPlatillo);
        fab = root.findViewById(R.id.fab);
        mRecycler = root.findViewById(R.id.recyclerPlatillos);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FormDishesFragment());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FormDishesFragment());
            }
        });

        setUpRecyclerView();
        setupSearchView();
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

    private void setUpRecyclerView() {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        query = mFirestore.collection("platillos");

        FirestoreRecyclerOptions<Platillo> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Platillo>().setQuery(query, Platillo.class).build();

        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
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
        FirestoreRecyclerOptions<Platillo> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Platillo>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s).endAt(s + "~"), Platillo.class).build();
        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}