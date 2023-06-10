package com.paqta.paqtafood.ui.recycler;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.paqta.paqtafood.R;
import com.paqta.paqtafood.model.ListElement;

public class DescriptionActivity extends AppCompatActivity {

    TextView titleDescriptionTextView, cityDescriptionTextView, statusDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");
        titleDescriptionTextView = findViewById(R.id.titleDescriptionTextView);
        cityDescriptionTextView = findViewById(R.id.cityDescriptionTextView);
        statusDescriptionTextView = findViewById(R.id.statusDescriptionTextView);

        titleDescriptionTextView.setText(element.getName());
        titleDescriptionTextView.setTextColor(Color.parseColor(element.getColor()));

        cityDescriptionTextView.setText(element.getCity());

        statusDescriptionTextView.setText(element.getStatus());
        statusDescriptionTextView.setTextColor(Color.GRAY);
    }
}