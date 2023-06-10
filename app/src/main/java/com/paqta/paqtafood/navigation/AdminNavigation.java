package com.paqta.paqtafood.navigation;

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
import android.os.MemoryFile;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.ui.admin.category.CategoryFragment;
import com.paqta.paqtafood.ui.admin.dashboard.DashboardFragment;
import com.paqta.paqtafood.ui.admin.desserts.DessertsFragment;
import com.paqta.paqtafood.ui.admin.dishes.DishesFragment;
import com.paqta.paqtafood.ui.admin.drinks.DrinksFragment;
import com.paqta.paqtafood.ui.user.login.Login;
import com.paqta.paqtafood.ui.components.profile.ProfileFragment;
import com.paqta.paqtafood.ui.admin.staff.StaffFragment;

public class AdminNavigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null ) {
            comprobarUser();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.topAppBar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        replaceFragment(new DashboardFragment());
        navigationView.setCheckedItem(R.id.nav_sidebar_admin_dashboard);

        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userName);
        TextView email = headerView.findViewById(R.id.userEmail);

        username.setText(mUser.getDisplayName());
        email.setText(mUser.getEmail());

        toolbar.setOnMenuItemClickListener(item -> {
            int profile = R.id.nav_topbar_admin_profile;

            if (item.getItemId() == profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int admPlatos, admStaff, admExit, admCategories, admDashboard, admBebidas, admPostres;
            admPlatos = R.id.nav_sidebar_admin_platos;
            admStaff = R.id.nav_sidebar_admin_staff;
            admExit = R.id.nav_sidebar_admin_exit;
            admCategories = R.id.nav_sidebar_admin_categories;
            admDashboard = R.id.nav_sidebar_admin_dashboard;
            admBebidas = R.id.nav_sidebar_admin_bebidas;
            admPostres = R.id.nav_sidebar_admin_postres;

            if (item.getItemId() == admPlatos) {
                replaceFragment(new DishesFragment());
            } else if (item.getItemId() == admDashboard) {
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId() == admCategories) {
                replaceFragment(new CategoryFragment());
            } else if (item.getItemId() == admStaff) {
                replaceFragment(new StaffFragment());
            } else if (item.getItemId() == admBebidas) {
                replaceFragment(new DrinksFragment());
            } else if (item.getItemId() == admPostres) {
                replaceFragment(new DessertsFragment());
            } else if (item.getItemId() == admExit) {
                mAuth.signOut();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                Toast.makeText(this, "Cerrando sesion", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
    }

    private void comprobarUser() {
        mUser = mAuth.getCurrentUser();
        mUser.getEmail();

        mFirestore.collection("usuarios").document(mUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String rol = documentSnapshot.get("rol").toString();
                        if (!rol.equals("Administrador")) {
                            Intent intent = new Intent(AdminNavigation.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminNavigation.this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
                    }
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}