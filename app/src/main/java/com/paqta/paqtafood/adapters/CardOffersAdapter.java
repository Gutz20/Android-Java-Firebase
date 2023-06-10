package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.paqta.paqtafood.ui.user.offers.components.content_offers.ContentOffersFragment;

public class CardOffersAdapter extends FirestoreRecyclerAdapter<Producto, CardOffersAdapter.ViewHolder> {

    Activity activity;
    FragmentManager fm;

    public CardOffersAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardOffersAdapter.ViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.detailsOffer.setText(TextUtils.join("\n", model.getDetalles()));
        Glide.with(holder.imageOffer.getContext()).load(model.getImagen()).into(holder.imageOffer);

        holder.btnDetailOffer.setOnClickListener(v -> {
            ContentOffersFragment fragment = new ContentOffersFragment();

            Bundle bundle = new Bundle();
            bundle.putString("idOffer", id);
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @NonNull
    @Override
    public CardOffersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_offer, parent, false);
        return new CardOffersAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView detailsOffer;
        private ImageView imageOffer;
        private MaterialButton btnDetailOffer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detailsOffer = itemView.findViewById(R.id.detailsOffer);
            imageOffer = itemView.findViewById(R.id.imagenOffer);
            btnDetailOffer = itemView.findViewById(R.id.btnDetailOffer);
        }
    }
}
