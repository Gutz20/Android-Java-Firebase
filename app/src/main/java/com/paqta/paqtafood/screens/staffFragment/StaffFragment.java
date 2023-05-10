package com.paqta.paqtafood.screens.staffFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.paqta.paqtafood.R;

public class StaffFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StaffFragment() {
    }

    public static StaffFragment newInstance(String param1, String param2) {
        StaffFragment fragment = new StaffFragment();
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

    ListView listViewData;
    ArrayAdapter<String> adapter;
    String[] arrayProductos = {"Cabin in the Woods", "Insidious", "Sinister",
            "The Conjuring", "Get out", "Hereditary", "Doctor Sleep",
            "IT", "SAW", "Invasion of the Body Snatchers"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff, container, false);

        listViewData = root.findViewById(R.id.listview_data);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, arrayProductos);
        listViewData.setAdapter(adapter);

        return root;
    }

}