package com.paqta.paqtafood.ui.admin.category;

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
import com.paqta.paqtafood.adapters.CategoryAdapter;
import com.paqta.paqtafood.model.Categoria;
import com.paqta.paqtafood.ui.admin.category.components.FormCategoryFragment;


public class CategoryFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    FloatingActionButton fab;
    RecyclerView mRecycler;
    CategoryAdapter mAdapter;
    SearchView searchView;
    Query query;
    Button btnAdd, btnViewState;
    private boolean mostrarHabilitados = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();

        btnAdd = view.findViewById(R.id.btnAddCategory);
        searchView = view.findViewById(R.id.search);
        btnViewState = view.findViewById(R.id.btnViewState);
        fab = view.findViewById(R.id.fab);
        mRecycler = view.findViewById(R.id.recyclerCategorias);

        query = mFirestore.collection("categorias");

        fab.setOnClickListener(v -> replaceFragment(new FormCategoryFragment()));
        btnAdd.setOnClickListener(v -> replaceFragment(new FormCategoryFragment()));

        btnViewState.setOnClickListener(v -> {
            if (mostrarHabilitados) {
                setUpRecyclerView(query.whereEqualTo("estado", false));
                btnViewState.setText("Ver Categorias habilitados");
                mAdapter.startListening();
            } else {
                setUpRecyclerView(query.whereEqualTo("estado", true));
                btnViewState.setText("Ver Categorias inhabilitados");
                mAdapter.startListening();
            }
            mostrarHabilitados = !mostrarHabilitados;
        });

        setUpRecyclerView(query.whereEqualTo("estado", true));
        setupSearchView();
    }

    private void setUpRecyclerView(Query query) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecycler.setLayoutManager(linearLayoutManager);

        FirestoreRecyclerOptions<Categoria> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Categoria>().setQuery(query, Categoria.class).build();

        mAdapter = new CategoryAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
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
        FirestoreRecyclerOptions<Categoria> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Categoria>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s).endAt(s + "~"), Categoria.class).build();

        mAdapter = new CategoryAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}