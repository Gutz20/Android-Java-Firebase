package com.paqta.paqtafood.screens.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
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
import com.paqta.paqtafood.utils.ChangeColorBar;


public class DefaultNavigationApp extends AppCompatActivity {

    ActivityDefaultNavigationAppBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    ChangeColorBar changeColorBar = new ChangeColorBar();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDefaultNavigationAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ProfileFragment());

//        NAVEGACION SUPERIOR

        binding.topAppBar.setNavigationIcon(R.drawable.baseline_menu_24);

        binding.topAppBar.setNavigationIcon(R.drawable.baseline_menu_24);
        binding.topAppBar.setTitle("Menu");
        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Mostrar el sidebar
                Toast.makeText(DefaultNavigationApp.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        binding.topAppBar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.search) {
                binding.topAppBar.setTitle("Mi Perfil");
                Toast.makeText(DefaultNavigationApp.this, "Search", Toast.LENGTH_SHORT).show();
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.more) {
                Toast.makeText(DefaultNavigationApp.this, "More", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

//        NAVEGACION INFERIOR
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

        // DRAWER NAVIGATION


//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//
//        drawerLayout.addDrawerListener(drawerToggle);
//        drawerToggle.syncState();
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            int homeDrawer = R.id.homeDrawer;
//            int contact = R.id.contact;
//            int gallery = R.id.gallery;
//            int about = R.id.about;
//            int login = R.id.login;
//            int share = R.id.share;
//            int rate_us = R.id.rate_us;
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//
//                if (item.getItemId() == homeDrawer) {
//                    Toast.makeText(DefaultNavigationApp.this, "Home Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == contact) {
//                    Toast.makeText(DefaultNavigationApp.this, "Contact Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == gallery) {
//                    Toast.makeText(DefaultNavigationApp.this, "Gallery Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == about) {
//                    Toast.makeText(DefaultNavigationApp.this, "About Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == login) {
//                    Toast.makeText(DefaultNavigationApp.this, "Login Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == share) {
//                    Toast.makeText(DefaultNavigationApp.this, "Home Selected", Toast.LENGTH_SHORT).show();
//                } else if (item.getItemId() == rate_us) {
//                    Toast.makeText(DefaultNavigationApp.this, "Home Selected", Toast.LENGTH_SHORT).show();
//                }
//
//                return false;
//            }
//        });

        // AUTENTICACION

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
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