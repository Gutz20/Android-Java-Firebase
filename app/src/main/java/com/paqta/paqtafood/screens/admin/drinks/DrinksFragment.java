package com.paqta.paqtafood.screens.admin.drinks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.admin.dishes.components.FormDishesFragment;

public class DrinksFragment extends Fragment {


    FloatingActionButton fab;
    Button btnAdd, btnViewDisable;
    RecyclerView mRecycler;
    PlatilloAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    SearchView searchView;
    Query query;
    private boolean mostrarHabilitados = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drinks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();

        mRecycler = view.findViewById(R.id.recyclerDrinks);
        searchView = view.findViewById(R.id.search);

        btnAdd = view.findViewById(R.id.btnAdd);
        btnViewDisable = view.findViewById(R.id.btnViewDisable);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));
        btnAdd.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Bebidas");

        btnViewDisable.setOnClickListener(v -> {
            if (mostrarHabilitados) {
                setUpRecyclerView(query.whereEqualTo("estado", false));
                btnViewDisable.setText("Ver Bebidas habilitados");
                mAdapter.startListening();
            } else {
                setUpRecyclerView(query.whereEqualTo("estado", true));
                btnViewDisable.setText("Ver Bebidas inhabilitados");
                mAdapter.startListening();
            }
            mostrarHabilitados = !mostrarHabilitados;
        });

        setUpRecyclerView(query.whereEqualTo("estado", true));
        setupSearchView();
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

    private void setUpRecyclerView(Query query) {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();

        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void textSearch(String s) {
        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s)
                                .endAt(s + "\uf8ff"), Producto.class).build();

        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.startListening();
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}