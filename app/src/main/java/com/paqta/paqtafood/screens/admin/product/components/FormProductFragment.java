package com.paqta.paqtafood.screens.admin.product.components;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.admin.product.ProductFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FormProductFragment extends Fragment {
    String id_prod, storage_path = "productos/*", photo = "photo";
    ImageView photo_prod;
    Button btn_add, btn_dialog_image, btn_remove_image;
    EditText edtTxtNombre, edtTxtDescripcion, edtTxtCategoria;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private Uri image_url;
    private Bitmap image_camera;

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
        View root = inflater.inflate(R.layout.fragment_form_product, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        edtTxtNombre = root.findViewById(R.id.edtTextNombre);
        edtTxtDescripcion = root.findViewById(R.id.edtTextDescripcion);
        edtTxtCategoria = root.findViewById(R.id.edtTextCategoria);

        btn_add = root.findViewById(R.id.btn_add);
        btn_remove_image = root.findViewById(R.id.btnDeleteImage);
        btn_dialog_image = root.findViewById(R.id.btnDialogImage);

        photo_prod = root.findViewById(R.id.imageProducto);

        btn_dialog_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Seleccione una opcion")
                        .setMessage("Puede seleccionar la imagen de su galeria o si quiere puede tomar una foto con la camara")
                        .setPositiveButton("Galeria", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openImageGallery();
                            }
                        })
                        // Se cambio el orden solamente por estetica ;)
                        .setNegativeButton("Camara", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openCamera();
                            }
                        })
                        .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        if (id_prod == null || id_prod.isEmpty()) {
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = edtTxtNombre.getText().toString().trim();
                    String descripcion = edtTxtDescripcion.getText().toString().trim();
                    String categoria = edtTxtCategoria.getText().toString().trim();

                    if (validar(nombre, descripcion, categoria)) {
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

                    if (validar(nombre, descripcion, categoria)) {
                        updateProduct(nombre, descripcion, categoria);
                    }
                }
            });
        }

        return root;
    }

    /**
     * Valida los campos y si la imagen es diferente que la de por defecto o en todo caso si es diferente a la anterior imagen
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     * @return
     */
    private boolean validar(String nombre, String descripcion, String categoria) {
        Drawable currentDrawable = photo_prod.getDrawable();
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.image_icon_124);
        Bitmap currentBitmap = ((BitmapDrawable) currentDrawable).getBitmap();
        Bitmap defaultBitmap = ((BitmapDrawable) defaultDrawable).getBitmap();

        if (nombre.isEmpty() || descripcion.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (photo_prod.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentBitmap.equals(defaultBitmap)) {
            Toast.makeText(getContext(), "Selecciona una imagen diferente", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Se piden los permisos para poder usar la camara del usuario
     */
    private void permisosCamara() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // El permiso de la cámara está concedido, puedes abrir la cámara
        } else {
            // El permiso de la cámara no está concedido, solicita el permiso al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Abre la galeria de imagenes
     */
    private void openImageGallery() {
        permisosCamara();
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/*");
        startActivityForResult(galeria, REQUEST_IMAGE_GALLERY);
    }

    /**
     * Abre la camara del celular
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                image_url = data.getData();
                Glide.with(getView()).load(image_url).into(photo_prod);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                image_camera = (Bitmap) data.getExtras().get("data");
                image_url = bitmapToUri(image_camera);
                Glide.with(getView()).load(image_url).into(photo_prod);
            }
        }
    }

    /**
     * Encargado de modificar el producto utilizando FireStorage
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     */
    private void updateProduct(String nombre, String descripcion, String categoria) {

        DocumentReference documentReference = mFirestore.collection("productos").document(id_prod);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("descripcion", descripcion);
        updates.put("categoria", categoria);

        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (image_url != null) {
                            subirImagen(documentReference);
                        }
                        Toast.makeText(getContext(), "El producto se modificó exitosamente", Toast.LENGTH_SHORT).show();
                        replaceFragment(new ProductFragment());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al modificar el producto", Toast.LENGTH_SHORT).show();
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

        DocumentReference documentReference = mFirestore.collection("productos").document();

        Map<String, Object> map = new HashMap<>();
        map.put("id", documentReference.getId());
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);
        map.put("categoria", categoria);

        documentReference.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        subirImagen(documentReference);
                        replaceFragment(new ProductFragment());
                    }
                });
    }

    /**
     * Se encarga de subir la imagen a FirebaseStorage con la referencia del documento que se envia por parametro
     *
     * @param documentReference
     */
    private void subirImagen(DocumentReference documentReference) {
//        String ruta_storage_foto = storage_path + "" + photo + "" + mAuth.getUid() + "" + documentReference.getId();
        String ruta_storage_foto = storage_path + "" + photo + "" + documentReference.getId();
        StorageReference imageRef = mStorage.getReference().child(ruta_storage_foto);

        UploadTask uploadTask = imageRef.putFile(image_url);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            documentReference.update("imagen", imageUrl)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "El documento se subió con su imagen exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error al subir el documento con su imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }
        });
    }


    /**
     * Obtiene el producto segun el id que fue enviado desde el boton de editar del anterior fragment
     * recuperado mediante el bundle que seria practicamente como el intent para los activity si no que
     * este pasa parametros a traves de los framents
     */
    private void getProduct() {
        mFirestore.collection("productos").document(id_prod).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombreProducto = documentSnapshot.getString("nombre");
                        String descripcionProducto = documentSnapshot.getString("descripcion");
                        String categoriaProducto = documentSnapshot.getString("categoria");
                        String imagenProducto = documentSnapshot.getString("imagen");

                        edtTxtNombre.setText(nombreProducto);
                        edtTxtDescripcion.setText(descripcionProducto);
                        edtTxtCategoria.setText(categoriaProducto);

                        if (imagenProducto == null) {
                            photo_prod.setImageResource(R.drawable.image_icon_124);
                        } else {
                            Glide.with(getView()).load(imagenProducto).into(photo_prod);
                            Snackbar.make(getView(), "Cargando Foto", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getView(), "Error al obtener los datos", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Remplaza el anterior fragment con otro
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Convierte un Bitmap a una URI
     *
     * @param bitmap
     * @return
     */
    private Uri bitmapToUri(Bitmap bitmap) {
        String nombreArchivo = "temp_image";
        File archivoTemporal = new File(getActivity().getCacheDir(), nombreArchivo + ".png");
        try (OutputStream os = new FileOutputStream(archivoTemporal)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider", archivoTemporal);
    }

}