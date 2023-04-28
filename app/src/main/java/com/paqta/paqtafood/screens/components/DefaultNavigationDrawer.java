package com.paqta.paqtafood.screens.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.paqta.paqtafood.R;

public class DefaultNavigationDrawer extends AppCompatActivity {
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
        setContentView(R.layout.activity_default_navigation_drawer);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            int homeDrawer = R.id.homeDrawer;
            int contact = R.id.contact;
            int gallery = R.id.gallery;
            int about = R.id.about;
            int login = R.id.login;
            int share = R.id.share;
            int rate_us = R.id.rate_us;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if (item.getItemId() == homeDrawer) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Home Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == contact) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Contact Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == gallery) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Gallery Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == about) {
                    Toast.makeText(DefaultNavigationDrawer.this, "About Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == login) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Login Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == share) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Home Selected", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == rate_us) {
                    Toast.makeText(DefaultNavigationDrawer.this, "Home Selected", Toast.LENGTH_SHORT).show();
                }

                return false;
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
        super.onBackPressed();
    }
}