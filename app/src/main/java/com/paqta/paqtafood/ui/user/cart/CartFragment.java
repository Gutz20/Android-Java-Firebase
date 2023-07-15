package com.paqta.paqtafood.ui.user.cart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardCartAdapter;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.model.User;
import com.paqta.paqtafood.ui.user.cart.components.SecondStepCartFragment;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements CardCartAdapter.OnCartItemRemovedListener{
    StepView stepView;
    TextView stepTextView;
    Button btnComprar;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    LinearLayout layoutDataCart;
    TextView textViewTotal, textViewSubtotal;
    RecyclerView recyclerView;
    CardCartAdapter cardCartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.rycCart);

        btnComprar = view.findViewById(R.id.btnComprar);
        layoutDataCart = view.findViewById(R.id.layoutDataCart);

        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewSubtotal = view.findViewById(R.id.textViewSubTotal);

        stepTextView = view.findViewById(R.id.stepTextView);
        stepView = view.findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        btnComprar.setOnClickListener(v -> {
            goToNextStep();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadCartItems();
    }

    private void loadCartItems() {
        mFirestore.collection("usuarios")
                .document(mUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> carrito = documentSnapshot.toObject(User.class).getCarrito();

                        if (carrito != null && !carrito.isEmpty()) {

                            mFirestore.collection("productos")
                                    .whereIn(FieldPath.documentId(), carrito)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        List<Producto> productoList = new ArrayList<>();
                                        double subtotal = 0.0;

                                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            Producto producto = snapshot.toObject(Producto.class);
                                            productoList.add(producto);


                                            subtotal += producto.getPrecio();
                                        }


                                        textViewSubtotal.setText(String.format("S/%.2f", subtotal));


                                        double total = subtotal;


                                        textViewTotal.setText(String.format("S/%.2f", total));

                                        setupRecycler(productoList);
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            setupRecycler(new ArrayList<>());
                            Toast.makeText(getActivity(), "No tienes nada en tu carrito 😔", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setupRecycler(new ArrayList<>());
                    }
                });

    }

    private void setupRecycler(List<Producto> productoList) {
        cardCartAdapter = new CardCartAdapter(getActivity(), productoList, getActivity().getSupportFragmentManager());
        cardCartAdapter.setOnCartItemRemovedListener(this);
        cardCartAdapter.setOnQuantityChangeListener(new CardCartAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChange(int position, int quantity) {
                updateTotal();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardCartAdapter);
        updateTotal();
    }

    private Double updateTotal() {
        double subtotal = 0.0;
        for (int i = 0; i < cardCartAdapter.getItemCount(); i++) {
            Producto producto = cardCartAdapter.getItem(i);
            int quantity = cardCartAdapter.getQuantity(i);
            subtotal += (producto.getPrecio() * quantity);
        }

        double total = subtotal; // En este caso, el total es igual al subtotal

        textViewTotal.setText(String.format("S/%.2f", total));
        textViewSubtotal.setText(String.format("S/%.2f", total));
        return total;
    }


    private void goToNextStep() {
        int currentStep = 2; // El número de paso al que quieres ir

        Fragment nextStepFragment = new SecondStepCartFragment();
        Bundle args = new Bundle();
        args.putInt("currentStep", currentStep);
        args.putDouble("totalCart", updateTotal());
        nextStepFragment.setArguments(args);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, nextStepFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCartItemRemoved() {
        updateTotal();
    }
}