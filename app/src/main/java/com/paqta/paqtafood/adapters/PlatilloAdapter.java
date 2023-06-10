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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.ui.admin.dishes.components.FormDishesFragment;
import com.paqta.paqtafood.api.ProductoAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlatilloAdapter extends FirestoreRecyclerAdapter<Producto, PlatilloAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    ProductoAPI productoService = Apis.getProductoService();

    public PlatilloAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }


    @Override
    protected void onBindViewHolder(@NonNull PlatilloAdapter.ViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.textViewTitulo.setText(model.getNombre());
        holder.textViewDescp.setText(model.getDescripcion());
        Glide.with(activity.getApplicationContext()).load(model.getImagen()).into(holder.imagen);

        holder.btnEliminar.setOnClickListener(v-> {
            if (model.getEstado()) {
                inhabilitarProducto(id);
            } else {
                eliminarProducto(id);
            }
        });

        holder.btnEditar.setOnClickListener(v -> editarProducto(id));

        holder.swState.setChecked(model.getEstado());
        holder.swState.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Activado
                habilitarProducto(id);
            } else {
                // Desactivado
                inhabilitarProducto(id);
            }
        });
    }

    private void eliminarProducto(String id) {
        Call<Boolean> call = productoService.delete(id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Producto Eliminado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(activity, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inhabilitarProducto(String id) {
//        String storage_path = "platillos/*", prefijo = "platillo";

        Call<Boolean> call = productoService.disable(id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Producto inhabilitado", Toast.LENGTH_SHORT).show();
                }
                eliminarReferenciaUsuario(id);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(activity, "Error al inhabilitar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                eliminarReferenciaUsuario(id);
            }
        });
    }

    private void habilitarProducto(String id) {
        Call<Boolean> call = productoService.enable(id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Producto habilitado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(activity, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editarProducto(String id) {
        FormDishesFragment formDishesFragment = new FormDishesFragment();

        Bundle bundle = new Bundle();
        bundle.putString("idPlatillo", id);
        formDishesFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, formDishesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void eliminarReferenciaUsuario(String id) {

        CollectionReference usersRef = mFirestore.collection("usuarios");

        usersRef.whereArrayContains("favoritos", id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = mFirestore.batch();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String usuarioId = documentSnapshot.getId();

                            batch.update(usersRef.document(usuarioId), "favoritos", FieldValue.arrayRemove(id));

                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(activity, "Se removió de los favoritos de los usuarios", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, "Error al remover de los favoritos de los usuarios", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        usersRef.whereArrayContains("carrito", id) // Reemplaza "carrito" con el nombre del campo en tu colección de usuarios
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = mFirestore.batch();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String usuarioId = documentSnapshot.getId();

                            batch.update(usersRef.document(usuarioId), "carrito", FieldValue.arrayRemove(id));
                        }

                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(activity, "Se removió del carrito de los usuarios", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Error al remover del carrito de los usuarios", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_platillo_single, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitulo, textViewDescp;
        MaterialButton btnEditar, btnEliminar;
        SwitchMaterial swState;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imagen);
            textViewTitulo = itemView.findViewById(R.id.titulo);
            textViewDescp = itemView.findViewById(R.id.descripcion);
            btnEditar = itemView.findViewById(R.id.btnDetail);
            btnEliminar = itemView.findViewById(R.id.btnDelete);
            swState = itemView.findViewById(R.id.swState);
        }
    }
}
