package com.paqta.paqtafood.screens.product;

import android.os.Bundle;

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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.ProductoAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.product.components.AddProductFragment;
import com.paqta.paqtafood.screens.profile.ProfileFragment;


public class ProductFragment extends Fragment {

    FloatingActionButton fab;
    Button btnAddProduct;

    RecyclerView mRecycler;
    ProductoAdapter mAdapter;
    FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        btnAddProduct = root.findViewById(R.id.btnAddProduct);
        fab = root.findViewById(R.id.fab);
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = root.findViewById(R.id.recyclerProductos);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = mFirestore.collection("productos");

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();

        mAdapter = new ProductoAdapter(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddProductFragment());
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddProductFragment());
            }
        });

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}