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
import com.paqta.paqtafood.ui.user.detail_dishes.DetailDishesFragment;

public class CardSearchAdapter extends FirestoreRecyclerAdapter<Producto, CardSearchAdapter.CardViewHolder> {

    Activity activity;
    FragmentManager fm;
    public CardSearchAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardSearchAdapter.CardViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nombreView.setText(model.getNombre());
        Glide.with(holder.imagenView.getContext()).load(model.getImagen()).into(holder.imagenView);

        holder.btnDetail.setOnClickListener(v -> detail(id));
        holder.btnDetailArrow.setOnClickListener(v -> detail(id));
    }

    private void detail(String id) {
        DetailDishesFragment fragment = new DetailDishesFragment();

        Bundle bundle = new Bundle();
        bundle.putString("idProd", id);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
        private MaterialButton btnDetail, btnDetailArrow;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.text_item_search);
            imagenView = itemView.findViewById(R.id.image_view_search);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnDetailArrow = itemView.findViewById(R.id.btnDetail2);
        }

    }
}
