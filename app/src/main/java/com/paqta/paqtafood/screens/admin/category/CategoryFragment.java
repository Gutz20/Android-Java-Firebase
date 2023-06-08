package com.paqta.paqtafood.screens.admin.category;

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
import com.paqta.paqtafood.adapters.CategoryAdapter;
import com.paqta.paqtafood.model.Categoria;
import com.paqta.paqtafood.screens.admin.category.components.FormCategoryFragment;


public class CategoryFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    FloatingActionButton fab;
    RecyclerView mRecycler;
    CategoryAdapter mAdapter;
    SearchView searchView;
    Query query;
    Button btnAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        btnAdd = root.findViewById(R.id.btnAddCategory);
        searchView = root.findViewById(R.id.search);
        fab = root.findViewById(R.id.fab);
        mRecycler = root.findViewById(R.id.recyclerCategorias);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FormCategoryFragment());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new FormCategoryFragment());
            }
        });

        setUpRecyclerView();
        setupSearchView();
        return root;
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecycler.setLayoutManager(linearLayoutManager);

        query = mFirestore.collection("categorias");

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