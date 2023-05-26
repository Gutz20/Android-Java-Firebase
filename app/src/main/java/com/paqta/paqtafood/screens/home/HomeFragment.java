package com.paqta.paqtafood.screens.home;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Producto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

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

        subirPDF();
        generarQR(qr);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String qrContent = result.getContents();
//            if (qrContent.equals()) {
//                descargarPDFFromStorage();
//            }
        }
    }

    private void subirPDF() {
        InputStream inputStream = getResources().openRawResource(R.raw.tarea_semanal_02);
        StorageReference pdfRef = mStorage.getReference().child("archivos/cartilla.pdf");

        pdfRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Toast.makeText(getContext(), "El archivo ya existe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                UploadTask uploadTask = pdfRef.putStream(inputStream);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void generarQR(ImageView imageView) {
        StorageReference storageReference = mStorage.getReference().child(storagePathPdfCartilla);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String storageUrl = uri.toString();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {

                    BitMatrix bitMatrix = multiFormatWriter.encode(storageUrl, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    imageView.setImageBitmap(bitmap);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            descargarPDFFromStorage();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error de obtener la URL del archivo
                e.printStackTrace();
            }
        });

    }

    private void descargarPDFFromStorage() {
        StorageReference storageRef = mStorage.getReference().child(storagePathPdfCartilla);
        File localFile = new File(getContext().getExternalFilesDir(null), "cartilla.pdf");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // El archivo PDF se ha descargado exitosamente
                Toast.makeText(getContext(), "PDF descargado correctamente", Toast.LENGTH_SHORT).show();

                // Aquí puedes abrir el archivo PDF o realizar cualquier otra acción necesaria
                // Por ejemplo, puedes abrirlo utilizando una aplicación de visor de PDF instalada en el dispositivo
                openPDF(localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al descargar el archivo PDF
                Toast.makeText(getContext(), "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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