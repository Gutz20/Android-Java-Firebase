package com.paqta.paqtafood.ui.user.cart.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.android.material.button.MaterialButton;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.ui.user.home.HomeFragment;

public class DetailTicketFragment extends Fragment {

    MaterialButton btnHome;

    CheckBox cbRecibido, cbConfirmado, cbOrdenado, cbCamino, cbEntregado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        initComponents();
    }

    private void initUI(View view) {
        btnHome = view.findViewById(R.id.btnHome);
        cbRecibido = view.findViewById(R.id.cbRecibido);
        cbConfirmado = view.findViewById(R.id.cbConfirmado);
        cbOrdenado = view.findViewById(R.id.cbOrdenado);
        cbCamino = view.findViewById(R.id.cbCamino);
        cbEntregado = view.findViewById(R.id.cbEntregado);
    }

    private void initComponents() {
        btnHome.setOnClickListener(v -> navigateToHome());
    }

    private void navigateToHome() {
        Fragment fragment = new HomeFragment();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}