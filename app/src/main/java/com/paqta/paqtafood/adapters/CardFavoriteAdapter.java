package com.paqta.paqtafood.adapters;

import android.app.Activity;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;

public class CardFavoriteAdapter extends FirestoreRecyclerAdapter<Producto, CardFavoriteAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Activity activity;

    public CardFavoriteAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity) {
        super(options);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardFavoriteAdapter.ViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.tituloView.setText(model.getNombre());
        Glide.with(holder.imagenView.getContext()).load(model.getImagen()).into(holder.imagenView);
        holder.precioView.setText(String.valueOf(model.getPrecio()));
        holder.iconFavorite.setChecked(true);
        holder.iconFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());
                    userRef.update("favoritos", FieldValue.arrayRemove(id))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    notifyItemRemoved(holder.getBindingAdapterPosition());
                                    Toast.makeText(activity, "Eliminado de la lista de favoritos", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "Error al eliminar de la lista de favoritos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
//        holder.iconFavorite.setOnClickListener(v -> {
//            mFirestore.collection("usuarios")
//                    .document(mUser.getUid())
//                    .update("favoritos", FieldValue.arrayRemove(id))
//                    .addOnSuccessListener(command -> {
//                        notifyItemRangeRemoved(0, holder.getBindingAdapterPosition());
//                        clear();
//                        Snackbar.make(v, "Removido de favoritos", Snackbar.LENGTH_SHORT)
//                                .setAnchorView(R.id.bottomNavigationView)
//                                .show();
//                        startListening();
//                    })
//                    .addOnFailureListener(e -> {
//                        Snackbar.make(v, "Error al remover de favoritos", Snackbar.LENGTH_SHORT)
//                                .setAnchorView(R.id.bottomNavigationView)
//                                .show();
//                    });
//        });
    }

    public void clear() {
        getSnapshots().clear();
    }

    @NonNull
    @Override
    public CardFavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_favorite, parent, false);
        return new ViewHolder(view);
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
