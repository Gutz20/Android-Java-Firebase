package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.user.detail_dishes.DetailDishesFragment;
import com.paqta.paqtafood.ui.user.offers.components.content_offers.ContentOffersFragment;

import java.util.Optional;

public class CardCartAdapter extends FirestoreRecyclerAdapter<Producto, CardCartAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Activity activity;
    FragmentManager fm;

    public CardCartAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, FragmentActivity activity, FragmentManager supportFragmentManager) {
        super(options);
        this.activity = activity;
        this.fm = supportFragmentManager;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull CardCartAdapter.ViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nameProduct.setText(model.getNombre());

        if (model.getDetalles() != null) {
            holder.detailProduct.setText(TextUtils.join("\n", model.getDetalles()));
        }
        Glide.with(holder.imagenProductCart.getContext()).load(model.getImagen()).into(holder.imagenProductCart);

        holder.btnDelete.setOnClickListener(v -> {
            mFirestore.collection("usuarios")
                    .document(mUser.getUid())
                    .update("carrito", FieldValue.arrayRemove(id))
                    .addOnSuccessListener(command -> {

                        Toast.makeText(activity, "Eliminado del carrito", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(activity, "Error al eliminar del carrito", Toast.LENGTH_SHORT).show();
                    });

        });

        holder.btnDetail.setOnClickListener(v -> verDetalle(model, id));
    }

    private void verDetalle(Producto model, String id) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        if (model.getDescuento() != null) {
            fragment = new ContentOffersFragment();
            bundle.putString("idOffer", id);
        } else {
            fragment = new DetailDishesFragment();
            bundle.putString("idProd", id);
        }
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @NonNull
    @Override
    public CardCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_product_cart, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenProductCart;
        TextView nameProduct, detailProduct;
        MaterialButton btnDelete, btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenProductCart = itemView.findViewById(R.id.imgProductCart);
            nameProduct = itemView.findViewById(R.id.txtNameProductCart);
            detailProduct = itemView.findViewById(R.id.textDetailProductCart);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnDelete = itemView.findViewById(R.id.btnDeleteFromCart);
        }
    }
}
