package com.paqta.paqtafood.screens.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.databinding.ActivityDefaultNavigationAppBinding;
import com.paqta.paqtafood.screens.cart.CartFragment;
import com.paqta.paqtafood.screens.favorite.FavoriteFragment;
import com.paqta.paqtafood.screens.home.HomeFragment;
import com.paqta.paqtafood.screens.login.Login;
import com.paqta.paqtafood.screens.menu.MenuFragment;
import com.paqta.paqtafood.screens.profile.ProfileFragment;
import com.paqta.paqtafood.screens.search.SearchFragment;
import com.paqta.paqtafood.utils.ChangeColorBar;


public class DefaultNavigationApp extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    ActivityDefaultNavigationAppBinding binding;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    NavigationView navigationView;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDefaultNavigationAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

//        binding.navView.setCheckedItem(R.id.search_item);
//        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                if (item.getItemId() == R.id.nav_home) {
//                    replaceFragment(new HomeFragment());
//                } else if (item.getItemId() == R.id.nav_settings) {
//                    replaceFragment(new SearchFragment());
//                } else if (item.getItemId() == R.id.nav_share) {
//                    replaceFragment(new ProfileFragment());
//                } else if (item.getItemId() == R.id.nav_about) {
//                    replaceFragment(new MenuFragment());
//                } else if (item.getItemId() == R.id.nav_logout) {
//                    Toast.makeText(DefaultNavigationApp.this, "Logout", Toast.LENGTH_SHORT).show();
//                }
//
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return false;
//            }
//        });

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int home, shorts, subscription, library;
            home = R.id.home;
            shorts = R.id.shorts;
            subscription = R.id.subscriptions;
            library = R.id.library;

            if (item.getItemId() == home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == shorts) {
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == subscription) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == library) {
                replaceFragment(new SearchFragment());
            }

            return true;
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
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
                Toast.makeText(DefaultNavigationApp.this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(DefaultNavigationApp.this, "Create a short is Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(DefaultNavigationApp.this, "Go live is Clicked", Toast.LENGTH_SHORT).show();
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

    // METODO DE PRUEBA
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle bundle =new Bundle();
        if (item.getItemId() == R.id.accelerator_item) {
            // Manually build the NavOptions that manually do
            // what NavigationUI.onNavDestinationSelected does for you
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.rotation_item, false, true)
                    .setRestoreState(true)
                    .build();

            NavController navController = Navigation.findNavController(this,
                    R.id.frame_layout);

            navController.navigate(String.valueOf(R.id.search_item), navOptions);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}