package com.paqta.paqtafood.ui.user.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardMenuAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.drinks.DrinksMenuFragment;
import com.paqta.paqtafood.ui.user.menu_detail.MenuDetailFragment;
import com.paqta.paqtafood.ui.user.offers.OffersFragment;
import com.paqta.paqtafood.ui.user.search.SearchFragment;


public class MenuFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FirebaseFirestore mFirestore;
    private RecyclerView mRecycler;
    CardMenuAdapter mAdapter;
    Query query;
    LinearLayout lrltNavPrincipal, lrltNavCombos, lrltNavExtras, lrltNavBebidas, lrltNavOffers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = root.findViewById(R.id.recyclerMenu);

        lrltNavCombos = root.findViewById(R.id.lrltToCombos);
        lrltNavExtras = root.findViewById(R.id.lrltToExtras);
        lrltNavPrincipal = root.findViewById(R.id.lrltToPrincipal);
        lrltNavBebidas = root.findViewById(R.id.lrltToBebidas);
        lrltNavOffers = root.findViewById(R.id.lrltToOfertas);

        lrltNavPrincipal.setOnClickListener(v -> replaceFragment(new MenuDetailFragment()));
        lrltNavCombos.setOnClickListener(v -> replaceFragment(new OffersFragment()));
        lrltNavExtras.setOnClickListener(v -> replaceFragment(new SearchFragment()));
        lrltNavBebidas.setOnClickListener(v -> replaceFragment(new DrinksMenuFragment()));
        lrltNavOffers.setOnClickListener(v -> replaceFragment(new OffersFragment()));

        setupRecyclerView();
        return root;
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecycler.setLayoutManager(linearLayoutManager);

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();
        mAdapter = new CardMenuAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
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
        fragmentTransaction.commit();
    }
}