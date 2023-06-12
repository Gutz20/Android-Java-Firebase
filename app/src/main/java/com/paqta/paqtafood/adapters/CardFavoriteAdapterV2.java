package com.paqta.paqtafood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;

import java.util.List;

public class CardFavoriteAdapterV2 extends RecyclerView.Adapter<CardFavoriteAdapterV2.ViewHolder> {

    private List<Producto> productosList;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public CardFavoriteAdapterV2(List<Producto> productosList) {
        this.productosList = productosList;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public CardFavoriteAdapterV2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardFavoriteAdapterV2.ViewHolder holder, int position) {
        Producto producto = productosList.get(position);
        holder.tituloView.setText(producto.getNombre());
        Glide.with(holder.imagenView.getContext()).load(producto.getImagen()).into(holder.imagenView);
        holder.precioView.setText(String.valueOf(producto.getPrecio()));
        holder.iconFavorite.setChecked(true);
        holder.iconFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    String productoId = producto.getId();

                    DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());
                    userRef.update("favoritos", FieldValue.arrayRemove(productoId))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Eliminación exitosa
                                    productosList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());

                                    Toast.makeText(holder.itemView.getContext(), "Producto removido de favoritos", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error al eliminar el producto de favoritos
                                    Toast.makeText(holder.itemView.getContext(), "Error al remover de favoritos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tituloView, precioView;
        private ImageView imagenView;
        private CheckBox iconFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloView = itemView.findViewById(R.id.title_view_favorite);
            precioView = itemView.findViewById(R.id.textViewPrecio);
            imagenView = itemView.findViewById(R.id.image_view_favorite);
            iconFavorite = itemView.findViewById(R.id.img_view_icon_favorite);
        }
    }
}
