package com.example.carwashelcatracho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.carwashelcatracho.Utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sessionManager = new SessionManager(this);
        
        // Set up Navigation Controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.isAdmin()) {
                navController.navigate(R.id.adminDashboardFragment);
            } else {
                navController.navigate(R.id.clientDashboardFragment);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}