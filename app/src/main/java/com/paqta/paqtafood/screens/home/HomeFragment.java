package com.paqta.paqtafood.screens.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.paqta.paqtafood.R;

public class HomeFragment extends Fragment {

    private static final String STORAGE_PATH_PDF_CARTILLA = "archivos/cartilla.pdf";
    private static final String STORAGE_EDITED_PDF_NAME = "cartilla_editada.pdf";
    private static final String TOAST_PDF_DOWNLOADED = "PDF descargado correctamente";
    private static final String TOAST_PDF_DOWNLOAD_FAILED = "Error al descargar el PDF";
    private static final int MAX_PRODUCTS_PER_PAGE = 5;
    String storagePathPdfCartilla = "archivos/cartilla.pdf";
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    ImageView qr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        qr = root.findViewById(R.id.imgQR2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        configurarPDF();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String qrContent = result.getContents();

            StorageReference storageRef = mStorage.getReference().child("archivos/cartilla.pdf");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String storageUrl = uri.toString();
                    if (qrContent.equals(storageUrl)) {
                        descargarPDFFromStorage();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Ocurrió un error al obtener la URL del archivo
                    Toast.makeText(getContext(), "Error al obtener la URL del archivo", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
        }
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

                qr.setImageBitmap(bitmap);
                qr.setOnClickListener(v -> descargarPDFFromStorage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void descargarPDFFromStorage() {
        StorageReference storageRef = mStorage.getReference().child(storagePathPdfCartilla);
        File localFile = new File(getContext().getExternalFilesDir(null), "cartilla.pdf");

        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getContext(), TOAST_PDF_DOWNLOADED, Toast.LENGTH_SHORT).show();
            openPDF(localFile);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), TOAST_PDF_DOWNLOAD_FAILED, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void openPDF(File file) {
        Uri fileUri = FileProvider.getUriForFile(getContext(), "com.paqta.paqtafood.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

}