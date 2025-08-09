package com.example.carwashelcatracho.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashelcatracho.Models.Notification;
import com.example.carwashelcatracho.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationVH> {
    private List<Notification> items;

    public NotificationsAdapter(List<Notification> items) {
        this.items = items;
    }

    public void setItems(List<Notification> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotificationVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationVH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class NotificationVH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvMensaje, tvFecha;

        NotificationVH(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }

        void bind(Notification n) {
            tvTitulo.setText(n.getTitulo());
            tvMensaje.setText(n.getMensaje());
            tvFecha.setText(n.getFecha());
        }
    }
}