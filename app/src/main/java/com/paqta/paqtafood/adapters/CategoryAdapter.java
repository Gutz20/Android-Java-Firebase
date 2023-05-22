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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Categoria;
import com.paqta.paqtafood.screens.category.components.FormCategoryFragment;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Categoria, CategoryAdapter.ViewHolder> {

    String storagePath = "categorias/*", prefijo = "category";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    Activity activity;
    FragmentManager fm;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Categoria> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position, @NonNull Categoria model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.tituloView.setText(model.getNombre());
        holder.descriptionView.setText(model.getDescripcion());
        Glide.with(activity.getApplicationContext()).load(model.getImagen()).into(holder.imageCat);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(id);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormCategoryFragment fragment = new FormCategoryFragment();

                Bundle bundle = new Bundle();
                bundle.putString("idCategory", id);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void deleteCategory(String id) {
        mFirestore.collection("categorias").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String rutaStorageFoto = storagePath + "" + prefijo + "" + id;
                        mStorage.getReference().child(rutaStorageFoto).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category_single, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tituloView, descriptionView;
        ImageView imageCat;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloView = itemView.findViewById(R.id.tituloCat);
            descriptionView = itemView.findViewById(R.id.descriptionCat);
            imageCat = itemView.findViewById(R.id.imageCat);
            btnEdit = itemView.findViewById(R.id.btnEditar);
            btnDelete = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
