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
import com.paqta.paqtafood.screens.dishes.DishesFragment;
import com.paqta.paqtafood.screens.favorite.FavoriteFragment;
import com.paqta.paqtafood.screens.home.HomeFragment;
import com.paqta.paqtafood.screens.menu.MenuFragment;
import com.paqta.paqtafood.screens.menu_detail.MenuDetailFragment;
import com.paqta.paqtafood.screens.offers.OffersFragment;
import com.paqta.paqtafood.screens.product.ProductFragment;
import com.paqta.paqtafood.screens.profile.ProfileFragment;
import com.paqta.paqtafood.screens.search.SearchFragment;
import com.paqta.paqtafood.screens.staffFragment.StaffFragment;


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
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_bottom_home) {
                toolbar.setTitle("INICIO PAQTAFOOD");
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == R.id.nav_bottom_favorite) {
                toolbar.setTitle("BLACK DAY");
                replaceFragment(new FavoriteFragment());
            } else if (item.getItemId() == R.id.nav_bottom_search) {
                toolbar.setTitle("BUSQUEDA");
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.nav_bottom_menu_detail) {
                toolbar.setTitle("BUSQUEMOS TU PLATILLOS");
                replaceFragment(new MenuDetailFragment());
            } else if (item.getItemId() == R.id.nav_bottom_cart) {
                toolbar.setTitle("BUSQUEDA");
                replaceFragment(new SearchFragment());
            }

            return true;
        });


        navigationView.setNavigationItemSelectedListener(item -> {
//            int search, rotation, accelerator, dashboard, dishes;
//            search = R.id.search_item;
//            rotation = R.id.rotation_item;
//            accelerator = R.id.accelerator_item;
//            dishes = R.id.dishes_item;
//
//
//            if (item.getItemId() == search) {
//                replaceFragment(new ProductFragment());
//            } else if (item.getItemId() == rotation) {
//                replaceFragment(new StaffFragment());
//            } else if (item.getItemId() == accelerator) {
//                replaceFragment(new MenuFragment());
//            } else if (item.getItemId() == dashboard) {
//                replaceFragment(new DishesFragment());
//            } else if (item.getItemId() == dishes) {
//                replaceFragment(new DishesFragment());
//            }
            return true;
        });
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

    /**
     * Muestra un dialog (ventana desplegable que nos dara opciones) practicamente
     * es un Bottom Sheets
     */
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(DefaultNavigationApp.this, "Opcion 1", Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(DefaultNavigationApp.this, "Opcion 2", Toast.LENGTH_SHORT).show();
            }
        });

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(DefaultNavigationApp.this, "Opcion 3", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}