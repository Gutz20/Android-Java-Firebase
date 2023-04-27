package com.paqta.paqtafood.presentation.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.utils.ChangeColorBar;

public class Profile extends AppCompatActivity {

    ChangeColorBar changeColorBar = new ChangeColorBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");
    }
}