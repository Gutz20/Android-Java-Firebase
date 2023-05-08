package com.paqta.paqtafood.screens.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.navigation.DefaultNavigationApp;
import com.paqta.paqtafood.screens.login.Login;
import com.paqta.paqtafood.utils.ChangeColorBar;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {

    TextInputEditText txtRegUser, txtRegPassword, txtRegConfirmPassword, txtRegEmail;
    ImageView imgViewSignup;
    TextView nuevoUsuario, labelRegistro;
    private FirebaseAuth mAuth;
    MaterialButton btnRegister;
    ImageView imageView;
    ChangeColorBar changeColorBar = new ChangeColorBar();

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

        imgViewSignup = findViewById(R.id.imageLogoSignup);
        labelRegistro = findViewById(R.id.tvwSignup);
        nuevoUsuario = findViewById(R.id.tvwSignupToLogin);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(v);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        });

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        });

        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");
    }

    public void validate(View v) {
        String email = txtRegEmail.getText().toString().trim();
        String password = txtRegPassword.getText().toString().trim();
        String confirmPassword = txtRegConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtRegEmail.setError("Correo Invalido");
            return;
        } else {
            txtRegEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            txtRegPassword.setError("Se necesitan más de 6 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            txtRegPassword.setError("Al menos un numero");
            return;
        } else {
            txtRegPassword.setError(null);
        }

        if (!confirmPassword.equals(confirmPassword)) {
            txtRegConfirmPassword.setError("Deben ser iguales");
            return;
        } else {
            registrar(v, email, password);
        }

    }

    public void registrar(View v, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Signup.this, DefaultNavigationApp.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(v, "Fallo en registrarse", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        transitionBack();
    }

    public void transitionBack() {
        Intent intent = new Intent(Signup.this, Login.class);

        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(imgViewSignup, "logoImageTrans");
        pairs[1] = new Pair<View, String>(labelRegistro, "textTrans");
        pairs[2] = new Pair<View, String>(txtRegEmail, "emailInputTextTrans");
        pairs[3] = new Pair<View, String>(txtRegPassword, "passwordInputTextTrans");
        pairs[4] = new Pair<View, String>(btnRegister, "buttonSignInTrans");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup.this, pairs);
        startActivity(intent, options.toBundle());
    }
}