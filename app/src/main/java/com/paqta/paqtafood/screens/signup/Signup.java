package com.paqta.paqtafood.screens.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paqta.paqtafood.MainActivity;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.login.Login;
import com.paqta.paqtafood.utils.ChangeColorBar;

public class Signup extends AppCompatActivity {

    TextInputEditText txtRegUser, txtRegPassword, txtRegConfirmPassword, txtRegEmail;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button btnRegister;
    ImageView imageView;
    ChangeColorBar changeColorBar = new ChangeColorBar();

    View contextView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        txtRegUser = findViewById(R.id.txtRegisterUser);
        txtRegPassword = findViewById(R.id.txtRegisterPassword);
        txtRegConfirmPassword = findViewById(R.id.txtRegisterConfirmPassword);
        txtRegEmail = findViewById(R.id.txtRegisterEmail);
        btnRegister = findViewById(R.id.btnRegister);
        imageView = findViewById(R.id.imgBackLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(txtRegEmail.getText());
                password = String.valueOf(txtRegPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Signup.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Signup.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Signup.this, "Cuenta creada.",
                                            Toast.LENGTH_SHORT).show();
                                    Snackbar.make(v, "Cuenta registrada.", Snackbar.LENGTH_LONG)
                                            .setAction("Dismiss", v1 -> {

                                            })
                                            .show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Signup.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                   Snackbar.make(v, "Registro fallido.", Snackbar.LENGTH_LONG)
                                           .setAction("Action", v1 -> {

                                           })
                                           .show();
                                }
                            }
                        });


            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });


        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");
    }
}