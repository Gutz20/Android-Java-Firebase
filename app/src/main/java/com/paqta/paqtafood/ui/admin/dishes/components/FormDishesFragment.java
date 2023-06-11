package com.paqta.paqtafood.ui.admin.dishes.components;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.ui.admin.desserts.DessertsFragment;
import com.paqta.paqtafood.ui.admin.dishes.DishesFragment;
import com.paqta.paqtafood.ui.admin.drinks.DrinksFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDishesFragment extends Fragment {
    String idPlatillo, storage_path = "platillos/*", prefijo = "platillo";
    ImageView fotoPlatillo;
    Button btnAdd, btnDialogImage, btnRemoveImage, btnAddContent;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    TextInputEditText edtTxtNombre, edtTxtDescripcion, edtTxtPrecio;
    ChipGroup chipGroup;
    ArrayList<String> listaContenido;
    AutoCompleteTextView autoCompleteTextView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private Uri imageUrl;
    private Bitmap imageCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPlatillo = getArguments().getString("idPlatillo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_dishes, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        fotoPlatillo = root.findViewById(R.id.imagePlatillo);
        edtTxtNombre = root.findViewById(R.id.edtTextNombre);
        edtTxtDescripcion = root.findViewById(R.id.edtTextDescripcion);
        edtTxtPrecio = root.findViewById(R.id.edtTextPrecio);
        autoCompleteTextView = root.findViewById(R.id.cmbCategoria);

        btnAdd = root.findViewById(R.id.btn_add);
        btnRemoveImage = root.findViewById(R.id.btnDeleteImage);
        btnDialogImage = root.findViewById(R.id.btnDialogImage);

        // Parte de los chips o contenido de los productos
        btnAddContent = root.findViewById(R.id.btnAddContent);
        chipGroup = root.findViewById(R.id.chipGroup);
        listaContenido = new ArrayList<>();

        btnDialogImage.setOnClickListener(v-> mostrarDialog());

        btnAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Agregar contenido");

                final EditText editText = new EditText(getContext());
                builder.setView(editText);

                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nuevoContenido = "+ " + editText.getText().toString();
                        agregarContenido(nuevoContenido);
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });

        procesarFormulario();
        setupDropdown();
        return root;
    }

    private void agregarContenido(String nuevoContenido) {
        listaContenido.add(nuevoContenido);
        mostrarContenido(listaContenido);
    }

    private void mostrarContenido(List<String> list) {
        chipGroup.removeAllViews();

        list.forEach(c -> {
            Chip chip = new Chip(getContext());
            chip.setText(c);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String contenido = chip.getText().toString();
                    list.remove(contenido);
                    mostrarContenido(list);
                }
            });

            chipGroup.addView(chip);
        });
    }

    private void procesarFormulario(){
        if (idPlatillo == null || idPlatillo.isEmpty()) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = edtTxtNombre.getText().toString().trim();
                    String descripcion = edtTxtDescripcion.getText().toString().trim();
                    String categoria = autoCompleteTextView.getText().toString().trim();
                    String precio = edtTxtPrecio.getText().toString();

                    if (validar(nombre, descripcion, categoria, listaContenido, precio)) {
                        postPlatillo(nombre, descripcion, categoria, Double.parseDouble(precio));
                    }
                }
            });
        } else {
            getPlatillo();
            btnAdd.setText("Actualizar");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nombre = edtTxtNombre.getText().toString().trim();
                    String descripcion = edtTxtDescripcion.getText().toString().trim();
                    String categoria = autoCompleteTextView.getText().toString().trim();
                    String precio = edtTxtPrecio.getText().toString();

                    if (validar(nombre, descripcion, categoria, listaContenido, precio)) {
                        updatePlatillo(nombre, descripcion, categoria, Double.parseDouble(precio));
                    }
                }
            });
        }
    }

    private void mostrarDialog() {
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

    private void setupDropdown() {
        List<String> opciones = new ArrayList<>();
        mFirestore.collection("categorias")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                            opciones.add(queryDocumentSnapshot.getData().get("nombre").toString());
                        });
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, opciones);
                        autoCompleteTextView.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al obtener las categorias", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Valida los campos y si la imagen es diferente que la de por defecto o en todo caso si es diferente a la anterior imagen
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     * @return
     */
    private boolean validar(String nombre, String descripcion, String categoria, List<String> detalles, String precio) {
        Drawable currentDrawable = fotoPlatillo.getDrawable();
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.image_icon_124);
        Bitmap currentBitmap = ((BitmapDrawable) currentDrawable).getBitmap();
        Bitmap defaultBitmap = ((BitmapDrawable) defaultDrawable).getBitmap();

        if (nombre.isEmpty() || descripcion.isEmpty() || categoria.isEmpty() || detalles.isEmpty() || precio.isEmpty()) {
            Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fotoPlatillo.getDrawable() == null) {
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
     * Encargado de modificar el platillo utilizando FireStorage
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     */
    private void updatePlatillo(String nombre, String descripcion, String categoria, Double precio) {

        DocumentReference documentReference = mFirestore.collection("productos").document(idPlatillo);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("searchField", nombre.toLowerCase());
        updates.put("descripcion", descripcion);
        updates.put("categoria", categoria);
        updates.put("detalles", listaContenido);
        updates.put("precio", precio);
        Timestamp timestamp = Timestamp.now();
        updates.put("updated_at", timestamp);

        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (imageUrl != null) {
                            subirImagen(categoria, documentReference);
                        }
                        Toast.makeText(getContext(), "Se modificó exitosamente", Toast.LENGTH_SHORT).show();
                        switch (categoria) {
                            case "Bebidas":
                                replaceFragment(new DrinksFragment());
                                break;
                            case "Postres":
                                replaceFragment(new DessertsFragment());
                                break;
                            case "Platillos":
                                replaceFragment(new DishesFragment());
                                break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al modificar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Encargado de añadir el platillo a firestore
     *
     * @param nombre
     * @param descripcion
     * @param categoria
     */
    private void postPlatillo(String nombre, String descripcion, String categoria, Double precio) {

        DocumentReference documentReference = mFirestore.collection("productos").document();

        Map<String, Object> map = new HashMap<>();
        map.put("id", documentReference.getId());
        map.put("nombre", nombre);
        map.put("descripcion", descripcion);
        map.put("categoria", categoria);
        map.put("detalles", listaContenido);
        map.put("precio", precio);
        map.put("disabled", true);
        map.put("searchField", nombre.toLowerCase());
        Timestamp timestamp = Timestamp.now();
        map.put("created_at", timestamp);
        map.put("updated_at", timestamp);

        documentReference.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        subirImagen(categoria, documentReference);
                        switch (categoria) {
                            case "Bebidas":
                                replaceFragment(new DrinksFragment());
                                break;
                            case "Postres":
                                replaceFragment(new DessertsFragment());
                                break;
                            case "Platillos":
                                replaceFragment(new DishesFragment());
                                break;
                        }
                    }
                });
    }


    /**
     * Obtiene el platillo segun el id que fue enviado desde el boton de editar del anterior fragment
     * recuperado mediante el bundle que seria practicamente como el intent para los activity si no que
     * este pasa parametros a traves de los framents
     */
    private void getPlatillo() {
        mFirestore.collection("productos").document(idPlatillo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombre = documentSnapshot.getString("nombre");
                        String descripcion = documentSnapshot.getString("descripcion");
                        String categoria = documentSnapshot.getString("categoria");
                        String imagen = documentSnapshot.getString("imagen");
                        Double precio = documentSnapshot.getDouble("precio");
                        List<String> detalles = (List<String>) documentSnapshot.get("detalles");

                        edtTxtNombre.setText(nombre);
                        edtTxtDescripcion.setText(descripcion);
                        edtTxtPrecio.setText(precio.toString());
                        listaContenido = (ArrayList<String>) detalles;
                        autoCompleteTextView.setText(categoria);
                        mostrarContenido(listaContenido);

                        if (imagen == null) {
                            fotoPlatillo.setImageResource(R.drawable.image_icon_124);
                        } else {
                            Glide.with(getView()).load(imagen).into(fotoPlatillo);
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
     * Se encarga de subir la imagen a FirebaseStorage con la referencia del documento que se envia por parametro
     *
     * @param documentReference
     */
    private void subirImagen(String categoria, DocumentReference documentReference) {
        switch (categoria) {
            case "Bebidas":
                storage_path = "bebidas/*";
                prefijo = "bebida";
                break;
            case "Postres":
                storage_path = "postres/*";
                prefijo = "postre";
                break;
            case "Platillos":
                storage_path = "platillos/*";
                prefijo = "platillo";
                break;
        }

        String ruta_storage_foto = storage_path + "" + prefijo + "" + documentReference.getId();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUrl = data.getData();
                Glide.with(getView()).load(imageUrl).into(fotoPlatillo);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageCamera = (Bitmap) data.getExtras().get("data");
                imageUrl = bitmapToUri(imageCamera);
                Glide.with(getView()).load(imageUrl).into(fotoPlatillo);
            }
        }
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