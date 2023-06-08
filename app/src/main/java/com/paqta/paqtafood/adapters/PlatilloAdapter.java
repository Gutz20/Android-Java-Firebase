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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.admin.dishes.components.FormDishesFragment;
import com.paqta.paqtafood.services.ProductoService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlatilloAdapter extends FirestoreRecyclerAdapter<Producto, PlatilloAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    Activity activity;
    FragmentManager fm;
    ProductoService productoService = Apis.getProductoService();

    public PlatilloAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }


    @Override
    protected void onBindViewHolder(@NonNull PlatilloAdapter.ViewHolder holder, int position, @NonNull Producto model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.nombre.setText(model.getNombre());
        holder.categoria.setText(model.getCategoria());
        Glide.with(activity.getApplicationContext()).load(model.getImagen()).into(holder.imagen);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPlatillo(v, id);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormDishesFragment formDishesFragment = new FormDishesFragment();

                Bundle bundle = new Bundle();
                bundle.putString("idPlatillo", id);
                formDishesFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, formDishesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void eliminarPlatillo(View view, String id) {
        String storage_path = "platillos/*", prefijo = "platillo";

        Call<Boolean> call =  productoService.disable(id);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Toast.makeText(activity, "Producto inhabilitado", Toast.LENGTH_SHORT).show();
                eliminarReferenciaUsuario(id);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(activity, "Error al inhabilitar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                eliminarReferenciaUsuario(id);
            }
        });
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, categoria;
        ImageView imagen, btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.namePlatillo);
            categoria = itemView.findViewById(R.id.categoriaPlatillo);
            imagen = itemView.findViewById(R.id.imagenPlatillo);
            btnDelete = itemView.findViewById(R.id.btnEliminarPlatillo);
            btnEdit = itemView.findViewById(R.id.btnEditarPlatillo);

        }
    }
}
