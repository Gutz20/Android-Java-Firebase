package com.paqta.paqtafood.presentation.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.presentation.components.DefaultBottomNavigation;
import com.paqta.paqtafood.presentation.dishes.Dishes;
import com.paqta.paqtafood.presentation.login.Login;
import com.paqta.paqtafood.presentation.signup.Signup;
import com.paqta.paqtafood.presentation.staff.Staff;
import com.paqta.paqtafood.utils.ChangeColorBar;

public class Principal extends AppCompatActivity {

    TextView textViewTitle;

    MaterialCardView mcvCard1, mcvCard2;
    ChangeColorBar changeColorBar = new ChangeColorBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textViewTitle = findViewById(R.id.textViewTitleMenu);
        mcvCard1 = findViewById(R.id.cardPlat_1);
        mcvCard2 = findViewById(R.id.cardPlat_2);

        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, DefaultBottomNavigation.class);
                startActivity(intent);
            }
        });

        mcvCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, Dishes.class);
                startActivity(intent);
            }
        });

        mcvCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, Staff.class);
                startActivity(intent);
            }
        });

        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");
    }
}