package com.paqta.paqtafood.presentation.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paqta.paqtafood.R;

public class Login extends AppCompatActivity {

    EditText txtUser, txtPassword;

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txtUser);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Este es el user: " + txtUser.getText().toString() + " " + txtPassword.getText().toString(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Login.this, Signup.class);
//                startActivity(intent);
            }
        });
    }
}