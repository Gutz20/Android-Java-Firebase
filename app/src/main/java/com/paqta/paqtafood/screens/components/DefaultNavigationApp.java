package com.paqta.paqtafood.screens.components;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.databinding.ActivityDefaultNavigationAppBinding;
import com.paqta.paqtafood.screens.cart.CartFragment;
import com.paqta.paqtafood.screens.favorite.FavoriteFragment;
import com.paqta.paqtafood.screens.login.Login;
import com.paqta.paqtafood.screens.menu.MenuFragment;
import com.paqta.paqtafood.screens.profile.ProfileFragment;
import com.paqta.paqtafood.screens.search.SearchFragment;


public class DefaultNavigationApp extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    ActivityDefaultNavigationAppBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDefaultNavigationAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MenuFragment());


        materialToolbar = findViewById(R.id.topAppBar);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DefaultNavigationApp.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        materialToolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.search) {
                Toast.makeText(DefaultNavigationApp.this, "Search", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.camera) {
                Toast.makeText(DefaultNavigationApp.this, "Camera", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.more) {
                Toast.makeText(DefaultNavigationApp.this, "More", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        // Se inicializa el binding para despues poder acceder a la navegacion inferior
        // y cambiar de fragment segun vamos seleccionando por el respectivo
        // fragment que el usuario selecciona

        // BADGE
        binding.bottomNavigationView.getOrCreateBadge(R.id.cart).setNumber(10);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int fastFood = R.id.menu;
            int search = R.id.search;
            int cart = R.id.cart;
            int favorite = R.id.favorites;

            if (item.getItemId() == fastFood) {
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == cart) {
                replaceFragment(new CartFragment());
            } else if (item.getItemId() == favorite) {
                replaceFragment(new FavoriteFragment());
            }

            return true;
        });

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    // Metodo que se encargara de remplazar el layout del contenido por el que nosotros
    // asignemos
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}