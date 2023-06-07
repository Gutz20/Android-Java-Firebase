package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.model.User;
import com.paqta.paqtafood.screens.staff.components.FormStaffFragment;
import com.paqta.paqtafood.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffAdapter extends FirestoreRecyclerAdapter<User, StaffAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    UserService userService = Apis.getUserService();
    Activity activity;
    FragmentManager fm;

    public StaffAdapter(@NonNull FirestoreRecyclerOptions<User> options, FragmentActivity activity, FragmentManager supportFragmentManager) {
        super(options);
        this.activity = activity;
        this.fm = supportFragmentManager;
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
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

        if (model.getImagen() == null) {
            Glide.with(holder.imageStaff.getContext())
                    .load("https://img.freepik.com/vector-premium/icono-circulo-usuario-anonimo-ilustracion-vector-estilo-plano-sombra_520826-1931.jpg")
                    .into(holder.imageStaff);
        } else {
            Glide.with(holder.imageStaff.getContext())
                    .load(model.getImagen())
                    .into(holder.imageStaff);
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

        holder.btnDelete.setOnClickListener(v -> {
            userService.eliminarUsuario(id);
            Call<Boolean> call = userService.inhabilitarUsuario(id);

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(activity, "Usuario Inhabilitado", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(activity, "Error al inhabilitar el usuario", Toast.LENGTH_SHORT).show();
                }
            });
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
            btnDelete = itemView.findViewById(R.id.btnDeleteStaff);
        }


    }
}
