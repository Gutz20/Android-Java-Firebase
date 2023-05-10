package com.paqta.paqtafood.screens.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.login.Login;


public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    TextView emailTextView, usernameTextView, phoneTextView, addressTextView, dniTextView;

    ShapeableImageView imageUser;
    MaterialButton btnLogout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        emailTextView = root.findViewById(R.id.emailTextView);
        btnLogout = root.findViewById(R.id.logoutButton);
        usernameTextView = root.findViewById(R.id.nameTextViewDesc);
        phoneTextView = root.findViewById(R.id.phoneTextViewDesc);
        addressTextView = root.findViewById(R.id.addressTextViewDesc);
        dniTextView = root.findViewById(R.id.dniTextViewDesc);
        imageUser = root.findViewById(R.id.imgProfileUser);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        DocumentReference docRef = db.collection("usuarios").document();
        if (user != null) {
            emailTextView.setText(user.getEmail());
            usernameTextView.setText(user.getDisplayName());
            phoneTextView.setText(user.getPhoneNumber());
            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).into(imageUser);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(root.getContext(), Login.class);
                startActivity(intent);
            }
        });

        return root;
    }

}
