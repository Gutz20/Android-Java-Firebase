package com.paqta.paqtafood.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.detail_dishes.DetailDishesFragment;

import java.util.List;

public class CardDrinksAdapter extends RecyclerView.Adapter<CardDrinksAdapter.ViewHolder> {

    private List<Producto> productosList;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    FragmentManager fm;

    public CardDrinksAdapter(List<Producto> productosList, FragmentManager fragmentManager) {
        this.productosList = productosList;
        this.fm = fragmentManager;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public CardDrinksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_drink_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardDrinksAdapter.ViewHolder holder, int position) {
        Producto producto = productosList.get(position);
        holder.cardView.setOnClickListener(v -> verDetalle(producto.getId()));
        holder.titulo.setText(producto.getNombre());
        holder.descripcion.setText(producto.getDescripcion());
        holder.precio.setText(String.valueOf(producto.getPrecio()));
        Glide.with(holder.imagen.getContext()).load(producto.getImagen()).into(holder.imagen);
    }

    private void verDetalle(String id) {
        Fragment fragment = new DetailDishesFragment();

        Bundle bundle = new Bundle();
        bundle.putString("idProd", id);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("detail_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView titulo, descripcion, precio;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardDrink);
            titulo = itemView.findViewById(R.id.tituloDrink);
            descripcion = itemView.findViewById(R.id.descripcionDrink);
            precio = itemView.findViewById(R.id.precioDrink);
            imagen = itemView.findViewById(R.id.imagenDrink);

        }

    }
}
