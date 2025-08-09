package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.User;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvNombre, tvApellido, tvEmail, tvTelefono, tvPais, tvTipoUsuario, tvFechaCreacion, tvFechaActualizacion, tvActivo;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvPais = view.findViewById(R.id.tvPais);
        tvTipoUsuario = view.findViewById(R.id.tvTipoUsuario);
        tvFechaCreacion = view.findViewById(R.id.tvFechaCreacion);
        tvFechaActualizacion = view.findViewById(R.id.tvFechaActualizacion);
        tvActivo = view.findViewById(R.id.tvActivo);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        loadProfile();
    }

    private void loadProfile() {
        showLoading(true);
        apiService.getClientProfile(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    tvNombre.setText(user.getNombreCompleto());
                    tvEmail.setText(user.getEmail());
                    tvTelefono.setText(user.getTelefono());
                    tvPais.setText(user.getPais());
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar el perfil", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}