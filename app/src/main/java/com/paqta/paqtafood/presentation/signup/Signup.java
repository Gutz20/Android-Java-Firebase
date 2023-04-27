package com.paqta.paqtafood.presentation.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.presentation.login.Login;
import com.paqta.paqtafood.presentation.principal.Principal;
import com.paqta.paqtafood.utils.ChangeColorBar;

public class Signup extends AppCompatActivity {

    EditText txtRegUser, txtRegPassword, txtRegConfirmPassword, txtRegEmail;

    Button btnRegister;

    ImageView imageView;

    ChangeColorBar changeColorBar = new ChangeColorBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtRegUser = findViewById(R.id.txtRegisterUser);
        txtRegPassword = findViewById(R.id.txtRegisterPassword);
        txtRegConfirmPassword = findViewById(R.id.txtRegisterConfirmPassword);
        txtRegEmail = findViewById(R.id.txtRegisterEmail);

        btnRegister = findViewById(R.id.btnRegister);

        imageView = findViewById(R.id.imgBackLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
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