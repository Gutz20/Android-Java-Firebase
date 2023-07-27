package com.paqta.paqtafood.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.api.Apis;
import com.paqta.paqtafood.api.PedidoAPI;
import com.paqta.paqtafood.model.Pedido;

public class PedidosAdapter extends FirestoreRecyclerAdapter<Pedido, PedidosAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore;

    PedidoAPI pedidoAPI = Apis.getPedidoService();
    Activity activity;
    FragmentManager fm;

    public PedidosAdapter(@NonNull FirestoreRecyclerOptions<Pedido> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull PedidosAdapter.ViewHolder holder, int position, @NonNull Pedido model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getBindingAdapterPosition());

    }

    @NonNull
    @Override
    public PedidosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
