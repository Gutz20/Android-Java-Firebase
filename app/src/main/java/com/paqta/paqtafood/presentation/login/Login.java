package com.paqta.paqtafood.presentation.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paqta.paqtafood.MainActivity;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.presentation.components.DefaultBottomNavigation;
import com.paqta.paqtafood.presentation.fragment.ProfileFragment;
import com.paqta.paqtafood.presentation.principal.Principal;
import com.paqta.paqtafood.presentation.profile.Profile;
import com.paqta.paqtafood.presentation.signup.Signup;
import com.paqta.paqtafood.utils.ChangeColorBar;

public class Login extends AppCompatActivity {

    EditText txtUser, txtPassword;
    Button btnLogin;

    TextView textViewRegister;

    ChangeColorBar changeColorBar = new ChangeColorBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txtLoginUser);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textViewRegister = findViewById(R.id.tvwRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtUser.getText().toString().equals("admin") && txtPassword.getText().toString().equals("123456")) {
                    Intent intent = new Intent(Login.this, Principal.class);
                    startActivity(intent);
                } else if (!txtUser.getText().toString().equals("admin") || !txtPassword.toString().equals("123456")) {
                    Toast.makeText(Login.this, "datos incorrectos", Toast.LENGTH_SHORT).show();
                } else if (txtUser.getText().toString().isEmpty() && txtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this, "Error datos vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });


        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");

    }


}