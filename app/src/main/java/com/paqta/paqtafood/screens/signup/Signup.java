package com.paqta.paqtafood.screens.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.screens.login.Login;
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

        // Acá se comprueba de igual manera en todos los campos que no se encuentren vacios
        // y tambien que se registre con unos datos que de momento son fijos ya que todavia
        // no relacionamos a una base de datos y solamente se comprueba con datos estaticos.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtRegUser.getText().toString().isEmpty()
                        || txtRegPassword.getText().toString().isEmpty()
                        || txtRegConfirmPassword.getText().toString().isEmpty()
                        || txtRegEmail.getText().toString().isEmpty()) {
                    Toast.makeText(Signup.this, "Uno o más campos esta vacio", Toast.LENGTH_SHORT).show();
                } else if (txtRegUser.getText().toString().equals("admin")
                        && txtRegPassword.getText().toString().equals("123456")
                        && txtRegConfirmPassword.getText().toString().equals(txtRegPassword.getText().toString())
                        && txtRegEmail.getText().toString().equals("admin@gmail.com")
                ) {
                    Toast.makeText(Signup.this, "Datos ingresados correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Signup.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
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