package com.paqta.paqtafood.screens.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.components.DefaultNavigationApp;
import com.paqta.paqtafood.screens.signup.Signup;
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


        // Se valida los datos ingresados por el usuario para comprobar que ingrese correctamente su informacion
        // en caso de que no esto no ocurra se le mostrara un mensaje que indica si los campos estan vacios
        // o en todo caso que los datos ingresados no son correcto
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtUser.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this, "Uno o más campos estan vacios", Toast.LENGTH_SHORT).show();
                } else if (txtUser.getText().toString().equals("admin") || txtPassword.getText().toString().equals("123456")) {
                    Intent intent = new Intent(Login.this, DefaultNavigationApp.class);
                    startActivity(intent);
                    Toast.makeText(Login.this, "Ingresando al sistema", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
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