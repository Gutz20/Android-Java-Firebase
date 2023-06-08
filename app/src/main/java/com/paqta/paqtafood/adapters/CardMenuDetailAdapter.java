package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.user.detail_dishes.DetailDishesFragment;

public class CardMenuDetailAdapter extends FirestoreRecyclerAdapter<Producto, CardMenuDetailAdapter.CardViewHolder> {

    Activity activity;
    FragmentManager fm;

    public CardMenuDetailAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardMenuDetailAdapter.CardViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nombreView.setText(model.getNombre());
        Glide.with(holder.imageView.getContext()).load(model.getImagen()).into(holder.imageView);
        holder.precioView.setText(String.valueOf(model.getPrecio()));
        holder.btnDetail.setOnClickListener(v -> {
            DetailDishesFragment detailDishesFragment = new DetailDishesFragment();

            Bundle bundle = new Bundle();
            bundle.putString("idProd", id);
            detailDishesFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, detailDishesFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @NonNull
    @Override
    public CardMenuDetailAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_menu_detail, parent, false);
        return new CardViewHolder(view);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreView, precioView;
        private ImageView imageView;
        private MaterialButton btnDetail;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.nombreMenuDetail);
            imageView = itemView.findViewById(R.id.imagenMenuDetail);
            precioView = itemView.findViewById(R.id.textViewPrecio);
            btnDetail = itemView.findViewById(R.id.viewDetail);
        }
    }
}
