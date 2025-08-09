package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String str(Object o) { return o == null ? "" : String.valueOf(o); }
    private int intVal(Object o) {
        try {
            if (o == null) return 0;
            if (o instanceof Number) return ((Number) o).intValue();
            return (int) Math.round(Double.parseDouble(String.valueOf(o)));
        } catch (Exception e) { return 0; }
    }
    private String formatMoney(Object o) {
        try {
            double v = 0.0;
            if (o instanceof Number) v = ((Number) o).doubleValue();
            else if (o != null) v = Double.parseDouble(String.valueOf(o));
            return "L. " + String.format("%.2f", v);
        } catch (Exception e) { return "L. 0.00"; }
    }

