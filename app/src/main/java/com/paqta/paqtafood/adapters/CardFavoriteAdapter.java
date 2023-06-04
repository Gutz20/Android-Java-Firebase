package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;

import java.util.List;

public class CardFavoriteAdapter extends FirestoreRecyclerAdapter<Producto, CardFavoriteAdapter.CardViewHolder> {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private List<String> listaFavoritos;

    public CardFavoriteAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, List<String> listaFavoritos) {
        super(options);
        this.listaFavoritos = listaFavoritos;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.tituloView.setText(model.getNombre());
        Glide.with(holder.imagenView.getContext()).load(model.getImagen()).into(holder.imagenView);

        boolean esFavorito = listaFavoritos.contains(id);
        holder.iconFavorite.setChecked(esFavorito);

        holder.iconFavorite.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (!isChecked) {

                mFirestore.collection("usuarios")
                        .document(mUser.getUid())
                        .update("favoritos", FieldValue.arrayRemove(id))
                        .addOnSuccessListener(command -> {
                            listaFavoritos.remove(id);
                            deleteItem(position);
                            Snackbar.make(buttonView, "Removido de favoritos", Snackbar.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(buttonView, "Error al remover de favoritos", Snackbar.LENGTH_SHORT).show();
                        });
            }
        }));
    }

    public void deleteItem(int posicion) {
        getSnapshots().getSnapshot(posicion).getReference().delete();
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
    }
}
