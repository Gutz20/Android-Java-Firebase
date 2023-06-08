package com.paqta.paqtafood.screens.user.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.screens.user.login.Login;
import com.paqta.paqtafood.services.UserService;
import com.paqta.paqtafood.utils.ChangeColorBar;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    TextInputEditText txtRegUser, txtRegPassword, txtRegConfirmPassword, txtRegEmail;
    ImageView imgViewSignup;
    TextView nuevoUsuario, labelRegistro;
    MaterialButton btnRegister;
    ImageView imageView;
    ChangeColorBar changeColorBar = new ChangeColorBar();
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    UserService userService = Apis.getUserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirestore = FirebaseFirestore.getInstance();
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
        String username = txtRegUser.getText().toString().trim();
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
            registrar(v, username, email, password);
        }

    }

    public void registrar(View v, String username, String email, String password) {

        HashMap<String, Object> map = new HashMap();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);

        Call<Boolean> call = userService.registrarUsuario(map);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(v, "Registrado con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(v, "Error al registrar el usuario", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Snackbar.make(v, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hasheo de contraseña
     *
     * @param base
     * @return
     */
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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