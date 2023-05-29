package com.paqta.paqtafood.screens.dishes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.PlatilloAdapter;
import com.paqta.paqtafood.model.Platillo;
import com.paqta.paqtafood.screens.dishes.components.FormDishesFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;


public class DishesFragment extends Fragment {

    private static final String STORAGE_PATH_PDF_CARTILLA = "archivos/cartilla.pdf";
    private static final String STORAGE_EDITED_PDF_NAME = "cartilla_editada.pdf";

    private static final int MAX_PRODUCTS_PER_PAGE = 5;
    FloatingActionButton fab;
    Button btnAdd, btnUpdateCartilla;
    RecyclerView mRecycler;
    PlatilloAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    SearchView searchView;
    Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dishes, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mRecycler = root.findViewById(R.id.recyclerPlatillos);
        searchView = root.findViewById(R.id.searchPlatillo);

        btnAdd = root.findViewById(R.id.btnAddPlatillo);
        btnUpdateCartilla = root.findViewById(R.id.btnUpdateCartilla);
        fab = root.findViewById(R.id.fab);


        fab.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));
        btnAdd.setOnClickListener(v -> replaceFragment(new FormDishesFragment()));
        btnUpdateCartilla.setOnClickListener(v -> configurarPDF());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setUpRecyclerView();
        setupSearchView();
        return root;
    }


    private void configurarPDF() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.cartilla_plantilla);
            File outputFile = new File(getContext().getFilesDir(), STORAGE_EDITED_PDF_NAME);

            PdfReader reader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

            int totalPages = reader.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                int imagenX = 100;
                int imagenY = 700;
                int textoX = 200;
                int textoY = 700;
                int descripcionX = 200;
                int descripcionY = 650;

                AtomicReference<PdfContentByte> canvas = new AtomicReference<>();
                CollectionReference reference = mFirestore.collection("productos");
                int finalI = i;
                reference.get().addOnSuccessListener(querySnapshot -> {
                    int index = 0;
                    int yPos = textoY;

                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String nombre = documentSnapshot.getString("nombre");
                        String descripcion = documentSnapshot.getString("descripcion");
                        String imagen = documentSnapshot.getString("imagen");

                        if (index % MAX_PRODUCTS_PER_PAGE == 0) {
                            // Crear una nueva página si es el primer producto o si se excedió el límite por página
                            if (canvas.get() != null) {
                                stamper.insertPage(finalI + 1, reader.getPageSizeWithRotation(1));
                                canvas.set(stamper.getOverContent(finalI + 1));
                            } else {
                                canvas.set(stamper.getOverContent(finalI));
                            }
                            yPos = textoY;
                        }
                        yPos -= 50;


                        // Agrega el nombre
                        Font nombreFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                        Phrase nombrePhrase = new Phrase(nombre, nombreFont);
                        ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, nombrePhrase, textoX, yPos, 0);

                        // Agrega la descripción
                        Font descripcionFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
                        Phrase descripcionPhrase = new Phrase(descripcion, descripcionFont);
                        ColumnText.showTextAligned(canvas.get(), Element.ALIGN_LEFT, descripcionPhrase, descripcionX, yPos - 20, 0);

                        // Agregar una imagen al PDF
                        Image image = null;
                        try {
                            image = Image.getInstance(imagen);
                            image.setAbsolutePosition(imagenX, yPos - 20); // Establece la posición de la imagen en la página
                            image.scaleToFit(100, image.getHeight()); // Ajusta el tamaño de la imagen
                            canvas.get().addImage(image);
                        } catch (IOException | DocumentException e) {
                            throw new RuntimeException(e);
                        }


                        index++;
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

    private void setUpRecyclerView() {
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        query = mFirestore.collection("productos").whereEqualTo("categoria", "Platillos");

        FirestoreRecyclerOptions<Platillo> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Platillo>().setQuery(query, Platillo.class).build();

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
        FirestoreRecyclerOptions<Platillo> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Platillo>()
                        .setQuery(query.orderBy("nombre")
                                .startAt(s).endAt(s + "~"), Platillo.class).build();
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