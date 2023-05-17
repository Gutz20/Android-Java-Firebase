package com.paqta.paqtafood.screens.product.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.ProductoAdapter;
import com.paqta.paqtafood.screens.product.ProductFragment;

import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment  {

    String id_prod;
    Button btn_add;
    EditText edtTxtNombre, edtTxtDescripcion, edtTxtCategoria;
    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_prod = getArguments().getString("id_prod");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_product, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        btn_add = root.findViewById(R.id.btn_add);
        edtTxtNombre = root.findViewById(R.id.edtTextNombre);
        edtTxtDescripcion = root.findViewById(R.id.edtTextDescripcion);
        edtTxtCategoria = root.findViewById(R.id.edtTextCategoria);

        if (id_prod == null || id_prod.isEmpty()) {
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = edtTxtNombre.getText().toString().trim();
                    String descripcion = edtTxtDescripcion.getText().toString().trim();
                    String categoria = edtTxtCategoria.getText().toString().trim();

                    if (nombre.isEmpty() && descripcion.isEmpty() && categoria.isEmpty()) {
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    } else {
                        postProduct(nombre, descripcion, categoria);
                    }
                }
            });
        } else {
            getProduct();
            btn_add.setText("Actualizar");
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = edtTxtNombre.getText().toString().trim();
                    String descripcion = edtTxtDescripcion.getText().toString().trim();
                    String categoria = edtTxtCategoria.getText().toString().trim();

                    if (nombre.isEmpty() && descripcion.isEmpty() && categoria.isEmpty()) {
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    } else {
                        updateProduct(nombre, descripcion, categoria);
                    }
                }
            });
        }

        return root;
    }

    private void updateProduct(String nombre, String descripcion, String categoria) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);
        map.put("categoria", categoria);

        mFirestore.collection("productos").document(id_prod).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        replaceFragment(new ProductFragment());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Encargado de añadir el producto a firestore
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     */
    private void postProduct(String nombre, String descripcion, String categoria) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);
        map.put("categoria", categoria);

        mFirestore.collection("productos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                replaceFragment(new ProductFragment());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProduct() {
        mFirestore.collection("productos").document(id_prod).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombreProducto = documentSnapshot.getString("nombre");
                        String descripcionProducto = documentSnapshot.getString("descripcion");
                        String categoriaProducto = documentSnapshot.getString("categoria");

                        edtTxtNombre.setText(nombreProducto);
                        edtTxtDescripcion.setText(descripcionProducto);
                        edtTxtCategoria.setText(categoriaProducto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}