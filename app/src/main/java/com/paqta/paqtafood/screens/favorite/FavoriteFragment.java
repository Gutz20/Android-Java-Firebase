package com.paqta.paqtafood.screens.favorite;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.model.Platillo;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselGravity;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteFragment extends Fragment {

    public FavoriteFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FirebaseFirestore mFirestore;

    RecyclerView mRecycler, mRecycler2, mRecycler3;
    List<CardFavoriteAdapter> adapters;
    Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = root.findViewById(R.id.carouselPlatillos);
        mRecycler2 = root.findViewById(R.id.carouselBebidas);
        mRecycler3 = root.findViewById(R.id.carouselPostres);
        adapters = new ArrayList<>();

        setupRecyclerView(mRecycler);
        setupRecyclerView(mRecycler2);
        setupRecyclerView(mRecycler3);
        return root;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Platillo> options = new FirestoreRecyclerOptions.Builder<Platillo>()
                .setQuery(query, Platillo.class)
                .build();

        CardFavoriteAdapter mAdapter = new CardFavoriteAdapter(options);
        mAdapter.notifyDataSetChanged();
        adapters.add(mAdapter);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapters.forEach(FirestoreRecyclerAdapter::startListening);
    }
    @Override
    public void onStop() {
        super.onStop();
        adapters.forEach(FirestoreRecyclerAdapter::stopListening);
    }

}