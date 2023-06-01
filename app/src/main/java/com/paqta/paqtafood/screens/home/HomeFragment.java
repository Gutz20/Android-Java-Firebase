package com.paqta.paqtafood.screens.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.paqta.paqtafood.R;

public class HomeFragment extends Fragment {

    private static final String TOAST_PDF_DOWNLOADED = "PDF descargado correctamente";
    private static final String TOAST_PDF_DOWNLOAD_FAILED = "Error al descargar el PDF";
    final long MAX_SIZE_BYTES = 1024 * 1024;
    String storagePathPdfCartilla = "archivos/cartilla.pdf";
    private FirebaseStorage mStorage;
    ImageView qr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mStorage = FirebaseStorage.getInstance();
        qr = root.findViewById(R.id.imgQR2);
        establecerQR();
        qr.setOnClickListener(v -> descargarPDFFromStorage());
        return root;
    }

    private void establecerQR() {
        StorageReference qrImageRef = mStorage.getReference().child("archivos").child("qr.jpg");

        // Error al obtener la imagen del código QR desde Firebase Storage
        qrImageRef.getBytes(MAX_SIZE_BYTES).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            qr.setImageBitmap(bitmap);
        }).addOnFailureListener(Throwable::printStackTrace);
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