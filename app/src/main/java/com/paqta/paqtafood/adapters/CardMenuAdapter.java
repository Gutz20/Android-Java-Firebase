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
import com.paqta.paqtafood.model.Platillo;
import com.paqta.paqtafood.screens.detail_dishes.DetailDishesFragment;
import com.paqta.paqtafood.screens.menu_detail.MenuDetailFragment;
import com.paqta.paqtafood.screens.product.components.FormProductFragment;

public class CardMenuAdapter extends FirestoreRecyclerAdapter<Platillo, CardMenuAdapter.CardViewHolder> {

    Activity activity;
    FragmentManager fm;
    public CardMenuAdapter(@NonNull FirestoreRecyclerOptions<Platillo> options, Activity activity, FragmentManager fragmentManager) {
        super(options);
        this.activity = activity;
        this.fm = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardMenuAdapter.CardViewHolder holder, int position, @NonNull Platillo model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nombreView.setText(model.getNombre());
        Glide.with(holder.imageView.getContext()).load(model.getImagen()).into(holder.imageView);
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inicializamos el fragment al que queremos ir
                DetailDishesFragment menuDetailFragment = new DetailDishesFragment();

                //Establecemos los argumentos que se enviaran
                Bundle bunble = new Bundle();
                bunble.putString("idProd", id);
                menuDetailFragment.setArguments(bunble);

                //Cambiamos el fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, menuDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @NonNull
    @Override
    public CardMenuAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_menu, parent, false);
        return new CardViewHolder(view);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton btnDetail;
        private TextView nombreView;
        private ImageView imageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.textNombrePlatilloMenu);
            imageView = itemView.findViewById(R.id.imagenPlatilloMenu);
            btnDetail = itemView.findViewById(R.id.viewDetailPlatilloMenu);
        }
    }
}
