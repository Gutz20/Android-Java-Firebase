package com.paqta.paqtafood.screens.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.login.Login;


public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    TextView emailTextView;
    MaterialButton btnLogout;

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null) {
//            Intent intent = new Intent(Profileactivity.this, Login.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        emailTextView = root.findViewById(R.id.emailTextView);
        btnLogout = root.findViewById(R.id.logoutButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());
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
