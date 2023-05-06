package com.paqta.paqtafood.screens.dishes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DishesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DishesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DishesFragment() {
        // Required empty public constructor
    }

    public static DishesFragment newInstance(String param1, String param2) {
        DishesFragment fragment = new DishesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText txtid, txtnom;
    private Button btnbus, btnmod, btnreg, btneli;
    private ListView lvDatos;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_dishes, container, false);

//        txtnom = (EditText) root.findViewById(R.id.txtnom);
//        btnbus = (Button) root.findViewById(R.id.btnbus);
//        btnmod = (Button) root.findViewById(R.id.btnmod);
//        btnreg = (Button) root.findViewById(R.id.btnreg);
//        btneli = (Button) root.findViewById(R.id.btneli);

//        botonBuscar();
//        botonModificar();
//        botonRegistrar();
//        botonEliminar();
//        getAllDishes();

        return root;
    }


//    private void botonBuscar() {
//    }
//
//    private void botonModificar() {
//    }
//
//    private void botonRegistrar() {
//        btnreg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, Object> user = new HashMap<>();
//                user.put("first", "Alan");
//                user.put("middle", "Mathison");
//                user.put("last", "Turing");
//                user.put("born", 1912);
//
//                db.collection("users")
//                        .add(user)
//                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentReference> task) {
//                                Toast.makeText(v.getContext(), "Agregado correctamente", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(v.getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//    }
//
//    private void getAllDishes() {
//        ArrayList<Dish> listDish = new ArrayList<>();
//        ArrayAdapter<Dish> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listDish);
//        lvDatos.setAdapter(adapter);
//
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document: task.getResult()) {
//                                Dish dish = document.toObject(Dish.class);
//
//                                listDish.add(dish);
//                                adapter.notifyDataSetChanged();
//                                Log.d("prueba", document.getId() + " => " + document.getData());
//                                Log.d("prueba", document.getId() + " => " + dish);
//                            }
//                        } else {
//                            Log.w("prueba", "Error getting documents." , task.getException());
//                        }
//                    }
//                });
//    }
//
//    private void botonEliminar() {
//
//    }

    private void ocultarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}