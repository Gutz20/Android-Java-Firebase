package com.paqta.paqtafood.screens.admin.staff.components;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.admin.staff.StaffFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormStaffFragment extends Fragment {

    String idStaff, storagePath = "usuarios/*", prefijo = "user";
    ImageView userFoto;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Button btnAdd, btnDialogImage;
    TextInputEditText edtTxtUsername, edtTxtPassword, edtTxtEmail;
    AutoCompleteTextView rolAutoComplete;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private Uri imageUrl;
    private Bitmap imageCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idStaff = getArguments().getString("idStaff");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_staff, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        edtTxtUsername = root.findViewById(R.id.edtTextUsername);
        edtTxtEmail = root.findViewById(R.id.edtTextEmail);
        edtTxtPassword = root.findViewById(R.id.edtTextPassword);
        rolAutoComplete = root.findViewById(R.id.cmbRol);

        userFoto = root.findViewById(R.id.imageStaff);

        btnAdd = root.findViewById(R.id.btnAdd);
        btnDialogImage = root.findViewById(R.id.btnDialogImage);

        btnDialogImage.setOnClickListener(v-> mostrarDialog());

        procesarFormulario();
        setupDropdown();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUrl = data.getData();
                Glide.with(getView()).load(imageUrl).into(userFoto);
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageCamera = (Bitmap) data.getExtras().get("data");
                imageUrl = bitmapToUri(imageCamera);
                Glide.with(getView()).load(imageUrl).into(userFoto);
            }
        }
    }

    private void setupDropdown() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Administrador");
        opciones.add("Usuario");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, opciones);
        rolAutoComplete.setAdapter(adapter);
    }

    private void procesarFormulario(){
        if (idStaff == null || idStaff.isEmpty()) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = edtTxtUsername.getText().toString().trim();
                    String password = edtTxtPassword.getText().toString().trim();
                    String rol = rolAutoComplete.getText().toString().trim();
                    String email = edtTxtEmail.getText().toString().trim();

                    if (validar(username, rol, password, email)) {
                        postStaff(username, rol, password, email);
                    }
                }
            });
        } else {
            getStaff();
            btnAdd.setText("Actualizar");
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = edtTxtUsername.getText().toString().trim();
                    String password = edtTxtPassword.getText().toString().trim();
                    String rol = rolAutoComplete.getText().toString().trim();
                    String email = edtTxtEmail.getText().toString().trim();

                    if (validar(username, rol, password, email)) {
                        updateStaff(username, rol, password, email);
                    }
                }
            });
        }
    }

    private void updateStaff(String username, String rol, String password, String email) {

        DocumentReference documentReference = mFirestore.collection("usuarios").document(idStaff);

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("rol", rol);
        updates.put("password", password);
        updates.put("email", email);

        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (imageUrl != null) {
                            subirImagen(documentReference);
                        }
                        Toast.makeText(getContext(), "El staff se modificó exitosamente", Toast.LENGTH_SHORT).show();
                        replaceFragment(new StaffFragment());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al modificar el staff", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void postStaff(String username, String rol, String password, String email) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mUser = mAuth.getCurrentUser();

                        DocumentReference documentReference = mFirestore.collection("usuarios").document(mUser.getUid());

                        Map<String, Object> map = new HashMap<>();
                        map.put("id", mUser.getUid());
                        map.put("username", username);
                        map.put("rol", rol);
                        map.put("password", password);
                        map.put("email", email);

                        documentReference.set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        subirImagen(documentReference);
                                        replaceFragment(new StaffFragment());
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Error al registrar el nuevo usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStaff() {
        mFirestore.collection("usuarios").document(idStaff).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String rol = documentSnapshot.getString("rol");
                        String password = documentSnapshot.getString("password");
                        String imagen = documentSnapshot.getString("imagen");
                        String email = documentSnapshot.getString("email");

                        edtTxtUsername.setText(username);
                        edtTxtEmail.setText(email);
                        edtTxtPassword.setText(password);


                        if (imagen == null) {
                            userFoto.setImageResource(R.drawable.image_icon_124);
                        } else {
                            Glide.with(getView()).load(imagen).into(userFoto);
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

    private boolean validar(String username, String rol, String password, String email) {
        Drawable currentDrawable = userFoto.getDrawable();
        Drawable defaultDrawable = getResources().getDrawable(R.drawable.image_icon_124);
        Bitmap currentBitmap = ((BitmapDrawable) currentDrawable).getBitmap();
        Bitmap defaultBitmap = ((BitmapDrawable) defaultDrawable).getBitmap();

        if (username.isEmpty() || rol.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userFoto.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentBitmap.equals(defaultBitmap)) {
            Toast.makeText(getContext(), "Selecciona una imagen diferente", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

    /**
     * Se encarga de subir la imagen a FirebaseStorage con la referencia del documento que se envia por parametro
     *
     * @param documentReference
     */
    private void subirImagen(DocumentReference documentReference) {
        String ruta_storage_foto = storagePath + "" + prefijo + "" + documentReference.getId();
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

    /**
     * Hasheo de contraseña
     *
     * @param base
     * @return
     */
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}