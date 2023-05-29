package com.paqta.paqtafood.screens.cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.adapters.CardFavoriteAdapter;
import com.paqta.paqtafood.adapters.CardSearchAdapter;
import com.paqta.paqtafood.model.Producto;
import com.shuhart.stepview.StepView;

import org.xml.sax.XMLFilter;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    StepView stepView;
    TextView stepTextView;
    Button btnReservar, btnComprar;
    RecyclerView rycrCartPlatillos, rycrCartBebidasPostres;

    CardSearchAdapter mAdapterPlatillos;
    CardFavoriteAdapter mAdapterBebidasPostres;
    LinearLayout layoutContaint;

    int stepIndex = 0;
    String[] stepsTexts = {"CARRITO", "ENTREGA", "METODO DE PAGO"};
    List<TextView> textViewList = new ArrayList<>();

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnReservar = root.findViewById(R.id.btnReservar);
        btnComprar = root.findViewById(R.id.btnComprar);

        rycrCartPlatillos = root.findViewById(R.id.cartPlatillos);
        rycrCartBebidasPostres = root.findViewById(R.id.cartBebidasPostres);

        layoutContaint = root.findViewById(R.id.linearLayoutContaint);

        stepTextView = root.findViewById(R.id.stepTextView);
        stepView = root.findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(3)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        btnComprar.setOnClickListener(v -> configureStep());

        configureRecyclers();
//        nextStep();
        return root;
    }

    private void configureRecyclers() {
        DocumentReference userRef = mFirestore.collection("usuarios").document(mUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> carrito = (List<String>) documentSnapshot.get("carrito");

                if (carrito != null && !carrito.isEmpty()) {
                    rycrCartPlatillos.setLayoutManager(new LinearLayoutManager(getContext()));

                    Query queryPlatillos = mFirestore.collection("productos")
                            .whereIn("id", carrito)
                            .whereEqualTo("categoria", "Platillos");

                    FirestoreRecyclerOptions<Producto> options = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(queryPlatillos, Producto.class)
                            .build();

                    mAdapterPlatillos = new CardSearchAdapter(options, getActivity(), getActivity().getSupportFragmentManager());
                    mAdapterPlatillos.notifyDataSetChanged();
                    rycrCartPlatillos.setAdapter(mAdapterPlatillos);
                    mAdapterPlatillos.startListening();

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    rycrCartBebidasPostres.setLayoutManager(linearLayoutManager);
                    Query queryBebidasPostres = mFirestore.collection("productos")
                            .whereIn("id", carrito)
                            .whereNotEqualTo("categoria", "Platillos");

                    FirestoreRecyclerOptions<Producto> options1 = new FirestoreRecyclerOptions.Builder<Producto>()
                            .setQuery(queryBebidasPostres, Producto.class)
                            .build();
                    mAdapterBebidasPostres = new CardFavoriteAdapter(options1);
                    mAdapterBebidasPostres.notifyDataSetChanged();
                    rycrCartBebidasPostres.setAdapter(mAdapterBebidasPostres);
                    mAdapterBebidasPostres.startListening();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterPlatillos != null && mAdapterBebidasPostres != null) {
            mAdapterPlatillos.stopListening();
            mAdapterBebidasPostres.stopListening();
        }
    }

    private void configureStep() {
        stepIndex++;
        if (stepIndex < stepsTexts.length) {
            stepTextView.setText(stepsTexts[stepIndex]);
            stepView.go(stepIndex, true);

            btnReservar.setVisibility(View.GONE);

            layoutContaint.removeAllViews();

            TextView textViewDireccion = new TextView(getActivity());
            textViewDireccion.setText("Direccion");

            TextView textViewPhone = new TextView(getActivity());
            textViewPhone.setText("Telefono");

            TextView textViewRef = new TextView(getActivity());
            textViewRef.setText("Referencia");

            textViewList.add(textViewDireccion);
            textViewList.add(textViewPhone);
            textViewList.add(textViewRef);
            textViewList.forEach(textView -> {
                textView.setTextColor(getResources().getColor(R.color.colorBlanco, getActivity().getTheme()));
                layoutContaint.addView(textView);
            });
        }
    }

    private void nextStep() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stepIndex++;
                if (stepIndex < stepsTexts.length) {
                    stepTextView.setText(stepsTexts[stepIndex]);
                    stepView.go(stepIndex, true);
                    nextStep();
                }
            }
        }, 3000);
    }
}