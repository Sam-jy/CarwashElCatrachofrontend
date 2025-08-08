package com.example.carwashelcatracho;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carwashelcatracho.Config.Personas;
import com.example.carwashelcatracho.R;

import java.util.ArrayList;

public class PersonAdapter extends BaseAdapter {

    Context context;
    ArrayList<Personas> list;

    public PersonAdapter(Context context, ArrayList<Personas> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(list.get(i).getId());
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);

        TextView name = view.findViewById(R.id.txtName);
        TextView phone = view.findViewById(R.id.txtPhone);
        ImageView imageView = view.findViewById(R.id.imgPerson);

        Personas persona = list.get(i);
        name.setText(persona.getNombre());
        phone.setText(persona.getTelefono() != null ? persona.getTelefono() : "Sin teléfono");

        String videoPath = persona.getVideo();
        if (videoPath != null && !videoPath.isEmpty()) {
            String videoUrl = "http://192.168.100.7/crud-php-person-examen/" + videoPath;
            Glide.with(context)
                .load(videoUrl)
                .thumbnail(0.1f)
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("persona", persona);
            if (context instanceof ListActivity) {
                ((ListActivity) context).startActivityForResult(intent, 100);
            } else {
                context.startActivity(intent);
            }
        });

        return view;
    }
}
