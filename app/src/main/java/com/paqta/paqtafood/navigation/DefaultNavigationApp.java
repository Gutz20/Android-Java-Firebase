package com.paqta.paqtafood.navigation;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.databinding.ActivityDefaultNavigationAppBinding;
import com.paqta.paqtafood.screens.cart.CartFragment;
import com.paqta.paqtafood.screens.category.CategoryFragment;
import com.paqta.paqtafood.screens.dishes.DishesFragment;
import com.paqta.paqtafood.screens.favorite.FavoriteFragment;
import com.paqta.paqtafood.screens.home.HomeFragment;
import com.paqta.paqtafood.screens.menu.MenuFragment;
import com.paqta.paqtafood.screens.menu_detail.MenuDetailFragment;
import com.paqta.paqtafood.screens.offers.OffersFragment;
import com.paqta.paqtafood.screens.product.ProductFragment;
import com.paqta.paqtafood.screens.profile.ProfileFragment;
import com.paqta.paqtafood.screens.search.SearchFragment;


public class DefaultNavigationApp extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    ActivityDefaultNavigationAppBinding binding;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDefaultNavigationAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        replaceFragment(new HomeFragment());

        toolbar.setOnMenuItemClickListener(item -> {
            int search;
            search = R.id.search;

            if (item.getItemId() == search) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });


        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_bottom_home) {
                toolbar.setTitle("Menú");
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == R.id.nav_bottom_favorite) {
                toolbar.setTitle("Favoritos");
                replaceFragment(new FavoriteFragment());
            } else if (item.getItemId() == R.id.nav_bottom_search) {
                toolbar.setTitle("Busquemos tu Platillo");
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.nav_bottom_menu_detail) {
                toolbar.setTitle("Menú");
                replaceFragment(new MenuDetailFragment());
            } else if (item.getItemId() == R.id.nav_bottom_cart) {
                toolbar.setTitle("BUSQUEDA");
                replaceFragment(new CartFragment());
            }

            return true;
        });


        navigationView.setNavigationItemSelectedListener(item -> {
            int offers, combos, perfil, cart, exit, qr, gestion_productos, gestion_platos, categorias;
            offers = R.id.nav_sidebar_offers;
            combos = R.id.nav_sidebar_combos;
            perfil = R.id.nav_sidebar_profile;
            cart = R.id.nav_sidebar_cart;
            exit = R.id.nav_sidebar_exit;
            qr = R.id.nav_sidebar_qr;
            gestion_productos = R.id.nav_sidebar_productos;
            gestion_platos = R.id.nav_sidebar_platos;
            categorias = R.id.nav_sidebar_categorias;

            if (item.getItemId() == offers) {
                replaceFragment(new OffersFragment());
            } else if (item.getItemId() == combos) {
                Toast.makeText(this, "Combos", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == perfil) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == cart) {
                replaceFragment(new CartFragment());
            } else if (item.getItemId() == exit) {
                Toast.makeText(this, "Cerrar sesion", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == qr) {
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == gestion_platos) {
                replaceFragment(new DishesFragment());
            } else if (item.getItemId() == gestion_productos) {
                replaceFragment(new ProductFragment());
            } else if (item.getItemId() == categorias) {
                replaceFragment(new CategoryFragment());
            }

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Metodo que se encargara de remplazar el layout del contenido por el que nosotros asignemos
     *
     * @param fragment, contenido
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}