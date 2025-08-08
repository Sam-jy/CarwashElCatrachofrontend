package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.carwashelcatracho.Models.User;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class ClientDashboardFragment extends Fragment {

    private TextView tvWelcome, tvUserName;
    private MaterialCardView cardVehicles, cardServices, cardCreateQuotation, 
                            cardQuotations, cardHistory, cardNotifications, cardProfile;
    private Toolbar toolbar;
    
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupToolbar();
        loadUserData();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvUserName = view.findViewById(R.id.tv_user_name);
        cardVehicles = view.findViewById(R.id.card_vehicles);
        cardServices = view.findViewById(R.id.card_services);
        cardCreateQuotation = view.findViewById(R.id.card_create_quotation);
        cardQuotations = view.findViewById(R.id.card_quotations);
        cardHistory = view.findViewById(R.id.card_history);
        cardNotifications = view.findViewById(R.id.card_notifications);
        cardProfile = view.findViewById(R.id.card_profile);
        toolbar = view.findViewById(R.id.toolbar);
        
        sessionManager = new SessionManager(requireContext());
    }

    private void setupToolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                performLogout();
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        User user = sessionManager.getUser();
        if (user != null) {
            tvUserName.setText(user.getNombreCompleto());
        }
    }

    private void setupClickListeners() {
        cardVehicles.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_vehicles);
        });

        cardServices.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_services);
        });

        cardCreateQuotation.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_quotations);
        });

        cardQuotations.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_quotations);
        });

        cardHistory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_history);
        });

        cardNotifications.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_notifications);
        });

        cardProfile.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_clientDashboard_to_profile);
        });
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigate(R.id.mainFragment);
    }
}
