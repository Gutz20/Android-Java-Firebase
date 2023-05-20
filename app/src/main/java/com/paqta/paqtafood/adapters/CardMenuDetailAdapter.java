package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Platillo;

public class CardMenuDetailAdapter extends FirestoreRecyclerAdapter<Platillo, CardMenuDetailAdapter.CardViewHolder> {

    public CardMenuDetailAdapter(@NonNull FirestoreRecyclerOptions<Platillo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardMenuDetailAdapter.CardViewHolder holder, int position, @NonNull Platillo model) {
        holder.bindData(model);
    }

    @NonNull
    @Override
    public CardMenuDetailAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_menu_detail, parent, false);
        return new CardViewHolder(view);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreView;
        private ImageView imageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.nombreMenuDetail);
            imageView = itemView.findViewById(R.id.imagenMenuDetail);
        }

        public void bindData(Platillo platillo) {
            nombreView.setText(platillo.getNombre());
            Glide.with(imageView.getContext()).load(platillo.getImagen()).into(imageView);
        }

    }
}
