package com.paqta.paqtafood.screens.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.login.Login;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    TextView emailTextView, usernameTextView, phoneTextView, addressTextView, dniTextView;
    private static final int PICK_IMAGE = 100;
    ShapeableImageView imageUser;
    MaterialButton btnLogout;
    FirebaseFirestore db;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    Uri imageUri;
    FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mUser = mAuth.getCurrentUser();
        storageReference = mStorage.getReference().child("perfil/" + mUser.getUid());

        emailTextView = root.findViewById(R.id.emailTextView);
        btnLogout = root.findViewById(R.id.logoutButton);
        usernameTextView = root.findViewById(R.id.nameTextViewDesc);
        phoneTextView = root.findViewById(R.id.phoneTextViewDesc);
        addressTextView = root.findViewById(R.id.addressTextViewDesc);
        dniTextView = root.findViewById(R.id.dniTextViewDesc);
        imageUser = root.findViewById(R.id.imgProfileUser);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        if (mUser != null) {
            emailTextView.setText(mUser.getEmail());
            usernameTextView.setText(mUser.getDisplayName());
            phoneTextView.setText(mUser.getPhoneNumber());
            Uri photoUrl = mUser.getPhotoUrl();
            Glide.with(this).load(photoUrl).into(imageUser);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(root);
            }
        });

        return root;
    }

    public void logout(View root) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(root.getContext(), Login.class);
        startActivity(intent);
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageUser.setImageURI(imageUri);
        }
    }
}
