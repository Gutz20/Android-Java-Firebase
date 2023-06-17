package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.detail_dishes.DetailDishesFragment;

import java.util.List;
import java.util.Locale;

public class CardCartAdapter extends RecyclerView.Adapter<CardCartAdapter.ViewHolder> {

    private List<Producto> productosList;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SharedPreferences sharedPreferences;
    Activity activity;
    FragmentManager fm;

    public CardCartAdapter(Activity activity, List<Producto> productosList, FragmentManager fragmentManager) {
        this.productosList = productosList;
        this.fm = fragmentManager;
        this.activity = activity;
        sharedPreferences = this.activity.getSharedPreferences("PrefsPaqtaFood", Context.MODE_PRIVATE);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public CardCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_product_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardCartAdapter.ViewHolder holder, int position) {
        Producto producto = productosList.get(position);
        Glide.with(holder.imagenProductCart.getContext()).load(producto.getImagen()).into(holder.imagenProductCart);

        holder.textViewCosto.setText(String.valueOf("Costo: S/" + (producto.getPrecio() * Integer.parseInt(holder.textViewCantidad.getText().toString()))));

        holder.addCantidad.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(holder.textViewCantidad.getText().toString());
            cantidad += 1;
            holder.textViewCantidad.setText(String.valueOf(cantidad));
            holder.textViewCosto.setText("Costo: S/" + cantidad * producto.getPrecio());

        });

        holder.subtractCantidad.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(holder.textViewCantidad.getText().toString());
            if (cantidad > 1) {
                cantidad -= 1;
                holder.textViewCantidad.setText(String.valueOf(cantidad));
                holder.textViewCosto.setText("Costo: S/" + cantidad * producto.getPrecio());
            }
        });


        holder.btnDetail.setOnClickListener(v -> verDetalle(producto.getId()));
        holder.btnDelete.setOnClickListener(v -> {
            String productoId = producto.getId();

            DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());
            userRef.update("carrito", FieldValue.arrayRemove(productoId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Eliminación exitosa
                            productosList.remove(holder.getBindingAdapterPosition());
                            notifyItemRemoved(holder.getBindingAdapterPosition());
                            notifyItemRangeChanged(holder.getBindingAdapterPosition(), getItemCount());

                            Toast.makeText(holder.itemView.getContext(), "Producto removido del carrito", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Error al remover del carrito", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void verDetalle(String id) {
        Fragment fragment = new DetailDishesFragment();
        ;
        Bundle bundle = new Bundle();
        bundle.putString("idProd", id);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenProductCart;
        TextView nameProduct, detailProduct, addCantidad, subtractCantidad, textViewCantidad, textViewCosto;
        MaterialButton btnDelete, btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenProductCart = itemView.findViewById(R.id.imgProductCart);
            nameProduct = itemView.findViewById(R.id.txtNameProductCart);
            detailProduct = itemView.findViewById(R.id.textDetailProductCart);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnDelete = itemView.findViewById(R.id.btnDeleteFromCart);
            addCantidad = itemView.findViewById(R.id.addCantidad);
            subtractCantidad = itemView.findViewById(R.id.subtractCantidad);
            textViewCantidad = itemView.findViewById(R.id.textViewCantidad);
            textViewCosto = itemView.findViewById(R.id.textViewCosto);
        }
    }
}
