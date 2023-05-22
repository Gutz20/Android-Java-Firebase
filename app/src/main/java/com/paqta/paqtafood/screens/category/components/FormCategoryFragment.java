package com.paqta.paqtafood.screens.category.components;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.category.CategoryFragment;
import com.paqta.paqtafood.screens.product.ProductFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FormCategoryFragment extends Fragment {
    String idCategory, storagePath = "categorias/*", prefijo="category";
    ImageView imageViewCategory;
    Button btnAdd, btnDialogImg, btnRemoveImg;
    TextInputEditText editTextNombre, editTextDescription;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private Uri imageUrl;
    private Bitmap image_camera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idCategory = getArguments().getString("idCategory");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_category, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        editTextNombre = root.findViewById(R.id.edtTextNombre);
        editTextDescription = root.findViewById(R.id.edtTextDescripcion);

        btnAdd = root.findViewById(R.id.btn_add);
        btnRemoveImg = root.findViewById(R.id.btnDeleteImage);
        btnDialogImg = root.findViewById(R.id.btnDialogImage);

        imageViewCategory = root.findViewById(R.id.imageCategory);

        if (idCategory == null || idCategory.isEmpty()) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = editTextNombre.getText().toString().trim();
                    String descripcion = editTextDescription.getText().toString().trim();

                    if (validar(nombre, descripcion)) {
                        postCategory(nombre, descripcion);
                    }
                }
            });
        } else {
            getCategory();
            btnAdd.setText("Actualizar");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = editTextNombre.getText().toString().trim();
                    String descripcion = editTextDescription.getText().toString().trim();

                    if (validar(nombre, descripcion)) {
                        updateCategory(nombre, descripcion);
                    }
                }
            });
        }

        setupDialog();
        return root;
    }

    private void updateCategory(String nombre, String descripcion) {
        DocumentReference documentReference = mFirestore.collection("categorias").document(idCategory);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("descripcion", descripcion);

        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (imageUrl != null) {
                            subirImagen(documentReference);
                        }
                        Toast.makeText(getContext(), "El producto se modificó exitosamente", Toast.LENGTH_SHORT).show();
                        replaceFragment(new CategoryFragment());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al modificar el producto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void postCategory(String nombre, String descripcion) {
        DocumentReference documentReference = mFirestore.collection("categorias").document();

        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);

        documentReference.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        subirImagen(documentReference);
                        replaceFragment(new CategoryFragment());
                    }
                });
    }

    private void subirImagen(DocumentReference documentReference) {
        String ruta_storage_foto = storagePath + "" + prefijo +  "" + documentReference.getId();
        StorageReference imageRef = mStorage.getReference().child(ruta_storage_foto);

        UploadTask uploadTask = imageRef.putFile(imageUrl);
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

    private void getCategory() {
        mFirestore.collection("categorias").document(idCategory).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombreCategoria = documentSnapshot.getString("nombre");
                        String descripcionCategoria = documentSnapshot.getString("descripcion");
                        String imagenCategoria = documentSnapshot.getString("imagen");

                        editTextNombre.setText(nombreCategoria);
                        editTextDescription.setText(descripcionCategoria);

                        if (imagenCategoria == null) {
                            imageViewCategory.setImageResource(R.drawable.image_icon_124);
                        } else {
                            Glide.with(getView()).load(imagenCategoria).into(imageViewCategory);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUrl = data.getData();
                Glide.with(getView()).load(imageUrl).into(imageViewCategory);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                image_camera = (Bitmap) data.getExtras().get("data");
                imageUrl = bitmapToUri(image_camera);
                Glide.with(getView()).load(imageUrl).into(imageViewCategory);
            }
        }
    }

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

    private void permisosCamara() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // El permiso de la cámara está concedido, puedes abrir la cámara
        } else {
            // El permiso de la cámara no está concedido, solicita el permiso al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean validar(String nombre, String descripcion) {
        Drawable currentDrawable = imageViewCategory.getDrawable();
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.image_icon_124);
        Bitmap currentBitmap = ((BitmapDrawable) currentDrawable).getBitmap();
        Bitmap defaultBitmap = ((BitmapDrawable) defaultDrawable).getBitmap();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageViewCategory.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentBitmap.equals(defaultBitmap)) {
            Toast.makeText(getContext(), "Selecciona una imagen diferente", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setupDialog() {
        btnDialogImg.setOnClickListener(new View.OnClickListener() {
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