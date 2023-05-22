package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Platillo;

public class CardOffersAdapter extends FirestoreRecyclerAdapter<Platillo, CardOffersAdapter.ViewHolder> {

    public CardOffersAdapter(@NonNull FirestoreRecyclerOptions<Platillo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardOffersAdapter.ViewHolder holder, int position, @NonNull Platillo model) {

    }

    @NonNull
    @Override
    public CardOffersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_favorite, parent, false);
        return new CardOffersAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
