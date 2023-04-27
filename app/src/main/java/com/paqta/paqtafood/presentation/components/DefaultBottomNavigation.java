package com.paqta.paqtafood.presentation.components;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.databinding.ActivityDefaultBottomNavigationBinding;
import com.paqta.paqtafood.presentation.cart.CartFragment;
import com.paqta.paqtafood.presentation.favorite.FavoriteFragment;
import com.paqta.paqtafood.presentation.fragment.ProfileFragment;
import com.paqta.paqtafood.presentation.menu.MenuFragment;
import com.paqta.paqtafood.presentation.search.SearchFragment;

public class DefaultBottomNavigation extends AppCompatActivity {

    ActivityDefaultBottomNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDefaultBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ProfileFragment());

//        BADGE
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
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}