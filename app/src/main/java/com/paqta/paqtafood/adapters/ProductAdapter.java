package com.paqta.paqtafood.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.paqta.paqtafood.R;
import com.google.android.material.card.MaterialCardView;
import com.paqta.paqtafood.model.Producto;
import com.paqta.paqtafood.screens.product.components.DetailProductFragment;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private Context context;
    private List<Producto> dataList;
    private ListAdapter.OnItemClickListener listener;

    public ProductAdapter(Context context, List<Producto> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setListener(ListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Glide.with(context).load(dataList.get(position).getImagen()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getNombre());
        holder.recDes.setText(dataList.get(position).getDescripcion());
        holder.recCategory.setText(dataList.get(position).getCategoria());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(context, DetailProductFragment.class);
//                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getImagen());
//                intent.putExtra("Descripcion", dataList.get(holder.getAdapterPosition()).getDescripcion());
//                intent.putExtra("Titulo", dataList.get(holder.getAdapterPosition()).getNombre());
//
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

class ProductViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recDes, recCategory;
    MaterialCardView recCard;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDes = itemView.findViewById(R.id.recDescription);
        recCategory = itemView.findViewById(R.id.recCategory);
    }
}
