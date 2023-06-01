package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.User;
import com.paqta.paqtafood.screens.staffFragment.components.FormStaffFragment;

public class StaffAdapter extends FirestoreRecyclerAdapter<User, StaffAdapter.ViewHolder> {

    Activity activity;
    FragmentManager fm;

    public StaffAdapter(@NonNull FirestoreRecyclerOptions<User> options, FragmentActivity activity, FragmentManager supportFragmentManager) {
        super(options);
        this.activity = activity;
        this.fm = supportFragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull StaffAdapter.ViewHolder holder, int position, @NonNull User model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nameStaff.setText(model.getUsername());
        if (model.getRol() == null) {
            holder.rolStaff.setText("usuario");
        } else {
            holder.rolStaff.setText(model.getRol());
        }

        if (model.getImage() == null) {
            Glide.with(holder.imageStaff.getContext())
                    .load("https://img.freepik.com/vector-premium/icono-circulo-usuario-anonimo-ilustracion-vector-estilo-plano-sombra_520826-1931.jpg")
                    .placeholder(R.drawable.baseline_person_24)
                    .into(holder.imageStaff);
        } else {
            Glide.with(holder.imageStaff.getContext()).load(model.getImage()).into(holder.imageStaff);
        }

        holder.btnDetail.setOnClickListener(v -> {
            FormStaffFragment fragment = new FormStaffFragment();

            Bundle bundle = new Bundle();
            bundle.putString("idStaff", id);

            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @NonNull
    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_staff_single, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageStaff;
        TextView nameStaff, rolStaff;
        MaterialButton btnDetail, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStaff = itemView.findViewById(R.id.imagenStaff);
            nameStaff = itemView.findViewById(R.id.textViewNameStaff);
            rolStaff = itemView.findViewById(R.id.textViewRolStaff);
            btnDetail = itemView.findViewById(R.id.btnDetailStaff);
        }


    }
}
