package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.carwashelcatracho.R;
import com.google.android.material.button.MaterialButton;

public class MainFragment extends Fragment {

    private MaterialButton btnLoginClient, btnLoginAdmin, btnRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
    }

    private void initViews(View view) {
        btnLoginClient = view.findViewById(R.id.btn_login_client);
        btnLoginAdmin = view.findViewById(R.id.btn_login_admin);
        btnRegister = view.findViewById(R.id.btn_register);
    }

    private void setupClickListeners() {
        btnLoginClient.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_main_to_loginClient);
        });

        btnLoginAdmin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_main_to_loginAdmin);
        });

        btnRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_main_to_register);
        });
    }
}
