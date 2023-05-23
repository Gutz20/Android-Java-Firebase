package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Platillo;

import java.util.ArrayList;
import java.util.List;

public class CardFavoriteAdapter extends FirestoreRecyclerAdapter<Platillo, CardFavoriteAdapter.CardViewHolder> {


    public CardFavoriteAdapter(@NonNull FirestoreRecyclerOptions<Platillo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Platillo model) {
        holder.bindData(model);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_favorite, parent, false);
        return new CardViewHolder(view);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView tituloView;
        private ImageView imagenView;
        private CheckBox iconFavorite;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloView = itemView.findViewById(R.id.title_view_favorite);
            imagenView = itemView.findViewById(R.id.image_view_favorite);
            iconFavorite = itemView.findViewById(R.id.img_view_icon_favorite);
        }

        public void bindData(Platillo cardFavorite) {
            tituloView.setText(cardFavorite.getNombre());
            Glide.with(imagenView.getContext()).load(cardFavorite.getImagen()).into(imagenView);
            iconFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Quitado de favoritos", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
