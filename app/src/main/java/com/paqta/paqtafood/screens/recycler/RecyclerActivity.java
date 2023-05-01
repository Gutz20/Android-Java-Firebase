package com.paqta.paqtafood.screens.recycler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.ListElement;
import com.paqta.paqtafood.utils.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {

    List<ListElement> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        init();
    }

    public void init() {
        elements = new ArrayList<>();
        elements.add(new ListElement("#775447", "Pedro", "Ica", "Activo"));
        elements.add(new ListElement("#607d8b", "Julio", "Lima", "Cancelado"));
        elements.add(new ListElement("#03a9f4", "Alejandra", "Lima", "Activo"));
        elements.add(new ListElement("#f44336", "Jessica", "Tumbes", "Inactivo"));
        elements.add(new ListElement("#009688", "Armando", "Yucatan", "Activo"));
        elements.add(new ListElement("#775447", "Pedro", "Ica", "Activo"));
        elements.add(new ListElement("#607d8b", "Julio", "Lima", "Cancelado"));
        elements.add(new ListElement("#03a9f4", "Alejandra", "Lima", "Activo"));
        elements.add(new ListElement("#f44336", "Jessica", "Tumbes", "Inactivo"));
        elements.add(new ListElement("#009688", "Armando", "Yucatan", "Activo"));

        ListAdapter listAdapter = new ListAdapter(elements, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}