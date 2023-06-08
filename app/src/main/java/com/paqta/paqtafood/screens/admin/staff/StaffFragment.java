package com.paqta.paqtafood.screens.admin.staff;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.StaffAdapter;
import com.paqta.paqtafood.model.User;
import com.paqta.paqtafood.screens.admin.staff.components.FormStaffFragment;

public class StaffFragment extends Fragment {
    RecyclerView rycStaff;
    Button btnAdd, btnSeeStaffDisable;
    private FirebaseFirestore mFirestore;
    StaffAdapter mAdapter;
    Query query;
    private boolean mostrarTodoElPersonal = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        rycStaff = root.findViewById(R.id.rycViewStaff);
        btnAdd = root.findViewById(R.id.btnAddStaff);
        btnSeeStaffDisable = root.findViewById(R.id.btnSeeDisableStaff);

        query = mFirestore.collection("usuarios");
        btnAdd.setOnClickListener(v -> replaceFragment(new FormStaffFragment()));
        btnSeeStaffDisable.setOnClickListener(v -> {
            if (mostrarTodoElPersonal) {
                setUpRecyclerView(query.whereEqualTo("estado", false));
                mAdapter.startListening();
            } else {
                setUpRecyclerView(query.whereEqualTo("estado", true));
                mAdapter.startListening();
            }
            mostrarTodoElPersonal = !mostrarTodoElPersonal;
        });

        setUpRecyclerView(query.whereEqualTo("estado", true));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void setUpRecyclerView(Query query) {
        rycStaff.setLayoutManager(new LinearLayoutManager(getContext()));

        FirestoreRecyclerOptions<User> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        mAdapter = new StaffAdapter(firestoreRecyclerOptions, getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.notifyDataSetChanged();
        rycStaff.setAdapter(mAdapter);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}