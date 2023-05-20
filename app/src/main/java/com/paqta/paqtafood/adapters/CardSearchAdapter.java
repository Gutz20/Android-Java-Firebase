package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Platillo;

public class CardSearchAdapter extends FirestoreRecyclerAdapter<Platillo, CardSearchAdapter.CardViewHolder> {

    public CardSearchAdapter(@NonNull FirestoreRecyclerOptions<Platillo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardSearchAdapter.CardViewHolder holder, int position, @NonNull Platillo model) {
        holder.bindData(model);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_search, parent, false);
        return new CardViewHolder(view);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreView;
        private ImageView imagenView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.text_item_search);
            imagenView = itemView.findViewById(R.id.image_view_search);
        }

        public void bindData(Platillo platillo) {
            nombreView.setText(platillo.getNombre());
            Glide.with(imagenView.getContext()).load(platillo.getImagen()).into(imagenView);
        }

    }
}
