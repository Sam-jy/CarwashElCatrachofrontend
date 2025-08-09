package com.example.carwashelcatracho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.carwashelcatracho.Utils.SessionManager;

import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        if (sessionManager.isLoggedIn()) {
            new android.os.Handler().post(() -> {
                if (sessionManager.isAdmin()) {
                    navController.navigate(R.id.adminDashboardFragment);
                } else {
                    navController.navigate(R.id.clientDashboardFragment);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}