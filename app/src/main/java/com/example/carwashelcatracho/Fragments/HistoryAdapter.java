package com.example.carwashelcatracho.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryVH> {
    private List<Quotation> items;

    public HistoryAdapter(List<Quotation> items) {
        this.items = items;
    }

    public void setItems(List<Quotation> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistoryVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class HistoryVH extends RecyclerView.ViewHolder {
        TextView tvServicio, tvVehiculo, tvFecha, tvPrecio, tvEstado;

        HistoryVH(@NonNull View itemView) {
            super(itemView);
            tvServicio = itemView.findViewById(R.id.tvServicio);
            tvVehiculo = itemView.findViewById(R.id.tvVehiculo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }

        void bind(Quotation q) {
            tvServicio.setText(q.getServicioNombre());
            tvVehiculo.setText(q.getMarca() + " " + q.getModelo() + " - " + q.getPlaca());
            tvFecha.setText(q.getFechaServicio());
            tvPrecio.setText(q.getPrecioFormateado());
            tvEstado.setText(q.getEstado());
        }
    }
}