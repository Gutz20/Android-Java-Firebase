package com.paqta.paqtafood.screens.admin.dishes;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.PlatilloAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.admin.dishes.components.FormDishesFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class DishesFragment extends Fragment {

    private static final String STORAGE_PATH_PDF_CARTILLA = "archivos/cartilla.pdf";
    private static final String STORAGE_EDITED_PDF_NAME = "cartilla_editada.pdf";

    FloatingActionButton fab;
    Button btnAdd, btnUpdateCartilla, btnViewState;
    RecyclerView mRecycler;
    PlatilloAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    SearchView searchView;
    Query query;

    private boolean mostrarHabilitados = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dishes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mRecycler = view.findViewById(R.id.recyclerPlatillos);
        searchView = view.findViewById(R.id.searchPlatillo);

        btnAdd = view.findViewById(R.id.btnAddPlatillo);
        btnUpdateCartilla = view.findViewById(R.id.btnUpdateCartilla);
        btnViewState = view.findViewById(R.id.btnViewState);
        fab = view.findViewById(R.id.fab);


        fab.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));
        btnAdd.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));
        btnUpdateCartilla.setOnClickListener(v -> configurarPDF());

        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        btnViewState.setOnClickListener(v -> {
            if (mostrarHabilitados) {
                setUpRecyclerView(query.whereEqualTo("estado", false));
                btnViewState.setText("Ver Platillos habilitados");
                mAdapter.startListening();
            } else {
                setUpRecyclerView(query.whereEqualTo("estado", true));
                btnViewState.setText("Ver Platillos inhabilitados");
                mAdapter.startListening();
            }
            mostrarHabilitados = !mostrarHabilitados;
        });

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        setUpRecyclerView(query.whereEqualTo("estado", true));
        setupSearchView();
    }

    private void configurarPDF() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.cartilla_paqta_food);
            File outputFile = new File(getContext().getFilesDir(), STORAGE_EDITED_PDF_NAME);

            PdfReader reader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

            int totalPages = reader.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                int textoPlatilloX = 90;
                int textoPlatilloY = 525;
                int textoPostreX = 90;
                int textoPostreY = 200;
                int textoBebibasX = 500;
                int textoBebidasY = 525;

                AtomicReference<PdfContentByte> canvas = new AtomicReference<>(stamper.getOverContent(i));
                CollectionReference reference = mFirestore.collection("productos");
                reference.get().addOnSuccessListener(querySnapshot -> {
                    int yPosPlatilloY = textoPlatilloY;
                    int yPosBebidaY = textoBebidasY;
                    int yPosPostreY = textoPostreY;

                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String nombre = documentSnapshot.getString("nombre");
                        String descripcion = documentSnapshot.getString("descripcion");
                        String categoria = documentSnapshot.getString("categoria");
//                        String imagen = documentSnapshot.getString("imagen");

                        String nombreFormat = nombre + "................... S/.";

                        Font nombreFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
                        nombreFont.setColor(BaseColor.WHITE);

                        Font descripcionFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
                        descripcionFont.setColor(BaseColor.WHITE);

                        if (Objects.equals(categoria, "Platillos")) {
                            yPosPlatilloY -= 30;
                            // Agrega el nombre
                            Phrase nombrePhrase = new Phrase(nombreFormat, nombreFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, nombrePhrase, textoPlatilloX, yPosPlatilloY, 0);
                            // Agrega la descripción
                            Phrase descripcionPhrase = new Phrase(descripcion, descripcionFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, descripcionPhrase, textoPlatilloX, yPosPlatilloY - 10, 0);
                        } else if (Objects.equals(categoria, "Bebidas")) {
                            yPosBebidaY -= 30;
                            // Agrega el nombre
                            Phrase nombrePhrase = new Phrase(nombreFormat, nombreFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, nombrePhrase, textoBebibasX, yPosBebidaY, 0);
                            // Agrega la descripción
                            Phrase descripcionPhrase = new Phrase(descripcion, descripcionFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, descripcionPhrase, textoBebibasX, yPosBebidaY - 10, 0);
                        } else if (Objects.equals(categoria, "Postres")) {
                            yPosPostreY -= 30;
                            // Agrega el nombre
                            Phrase nombrePhrase = new Phrase(nombreFormat, nombreFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, nombrePhrase, textoPostreX, yPosPostreY, 0);
                            // Agrega la descripción
                            Phrase descripcionPhrase = new Phrase(descripcion, descripcionFont);
                            ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, descripcionPhrase, textoPostreX, yPosPostreY - 10, 0);
                        }


                        // Agregar una imagen al PDF
                        // Image image = null;
                        // try {
                        //     image = Image.getInstance(imagen);
                        //     image.setAbsolutePosition(imagenX, yPos - 20); // Establece la posición de la imagen en la página
                        //     image.scaleToFit(100, image.getHeight()); // Ajusta el tamaño de la imagen
                        //     canvas.get().addImage(image);
                        // } catch (IOException | DocumentException e) {
                        //     throw new RuntimeException(e);
                        // }
                    }

                    try {
                        stamper.close();
                    } catch (DocumentException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    reader.close();

                    Toast.makeText(getContext(), "Se editó el PDF", Toast.LENGTH_SHORT).show();

                    StorageReference storageRef = mStorage.getReference().child(STORAGE_PATH_PDF_CARTILLA);
                    Uri fileUri = Uri.fromFile(outputFile);
                    UploadTask uploadTask = storageRef.putFile(fileUri);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        generarQR(storageRef);
                    }).addOnFailureListener(Throwable::printStackTrace);
                }).addOnFailureListener(Throwable::printStackTrace);
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private void generarQR(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String storageUrl = uri.toString();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {

                BitMatrix bitMatrix = multiFormatWriter.encode(storageUrl, BarcodeFormat.QR_CODE, 300, 300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference qrImageRef = mStorage.getReference().child("archivos/qr.jpg");

                UploadTask uploadTask = qrImageRef.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "Se actualizo el qr", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(Throwable::printStackTrace);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void setUpRecyclerView(Query query) {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();

        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(newText);
                return false;
            }
        });
    }

    private void textSearch(String s) {
        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s)
                                .endAt(s + "\uf8ff"), Producto.class).build();

        mAdapter = new PlatilloAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}