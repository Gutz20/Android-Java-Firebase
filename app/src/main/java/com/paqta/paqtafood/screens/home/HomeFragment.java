package com.paqta.paqtafood.screens.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.paqta.paqtafood.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {

    private static final String STORAGE_PATH_PDF_CARTILLA = "archivos/cartilla.pdf";
    private static final String STORAGE_EDITED_PDF_NAME = "cartilla_editada.pdf";
    private static final String TOAST_PDF_UPLOADED = "PDF editado subido correctamente";
    private static final String TOAST_PDF_UPLOAD_FAILED = "Error al subir el PDF editado";
    private static final String TOAST_PDF_EDITED = "Se editó el PDF";
    private static final String TOAST_PDF_DOWNLOADED = "PDF descargado correctamente";
    private static final String TOAST_PDF_DOWNLOAD_FAILED = "Error al descargar el PDF";
    String storagePathPdfCartilla = "archivos/cartilla.pdf";
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    ImageView qr;
    Button button;

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

        editarGuardarPDFyGenerarQR();
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

    private void editarGuardarPDFyGenerarQR() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.tarea_semanal_02);
            File outputFile = new File(getContext().getFilesDir(), STORAGE_EDITED_PDF_NAME);

            PdfReader reader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

            int totalPages = reader.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                PdfContentByte content = stamper.getOverContent(i);

                // Agregar texto en la posición deseada
                String texto = "Texto agregado desde Firebase en la página " + i;
                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
                Paragraph paragraph = new Paragraph(texto, font);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setSpacingAfter(10);
                paragraph.setIndentationLeft(50);

                ColumnText.showTextAligned(content, Element.ALIGN_LEFT, paragraph, 100, 100, 0);
            }

            stamper.close();
            reader.close();

            Toast.makeText(getContext(), "Se editó el PDF", Toast.LENGTH_SHORT).show();

            // Subir el archivo PDF editado a Firebase Storage
            StorageReference storageRef = mStorage.getReference().child(STORAGE_PATH_PDF_CARTILLA);
            Uri fileUri = Uri.fromFile(outputFile);
            UploadTask uploadTask = storageRef.putFile(fileUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
//                Toast.makeText(getContext(), "PDF editado subido correctamente", Toast.LENGTH_SHORT).show();
                // Generar el código QR después de que se haya subido el PDF editado
                generarQR(storageRef);
            }).addOnFailureListener(e -> {
//                Toast.makeText(getContext(), "Error al subir el PDF editado", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            });

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
//            Toast.makeText(getContext(), "Error al editar el PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void generarQR(StorageReference storageReference) {
//        StorageReference storageReference = mStorage.getReference().child(storagePathPdfCartilla);
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