package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;
import java.util.HashMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private TextView tvEmail, tvTipoUsuario, tvFechaCreacion, tvFechaActualizacion, tvActivo;
    private TextInputEditText etNombre, etApellido, etTelefono, etPais;
    private MaterialButton btnActualizar;
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
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTipoUsuario = view.findViewById(R.id.tvTipoUsuario);
        tvFechaCreacion = view.findViewById(R.id.tvFechaCreacion);
        tvFechaActualizacion = view.findViewById(R.id.tvFechaActualizacion);
        tvActivo = view.findViewById(R.id.tvActivo);
        
        etNombre = view.findViewById(R.id.etNombre);
        etApellido = view.findViewById(R.id.etApellido);
        etTelefono = view.findViewById(R.id.etTelefono);
        etPais = view.findViewById(R.id.etPais);
        btnActualizar = view.findViewById(R.id.btnActualizar);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        setupClickListeners();
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
                    bindUserData(user);
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

    private String formatDate(String dateStr) {
    if (dateStr == null) return "";
    try {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = inputFormat.parse(dateStr);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return outputFormat.format(date);
    } catch (ParseException e) {
        return dateStr;
    }
}

    private void setupClickListeners() {
        btnActualizar.setOnClickListener(v -> updateProfile());
    }

    private void bindUserData(User user) {
    tvEmail.setText(user.getEmail());
    tvTipoUsuario.setText(user.getTipoUsuario());
    tvFechaCreacion.setText(formatDate(user.getFechaCreacion()));
    tvFechaActualizacion.setText(formatDate(user.getFechaActualizacion()));
    tvActivo.setText(user.getActivo() == 1 ? "Sí" : "No");

    etNombre.setText(user.getNombre());
    etApellido.setText(user.getApellido());
    etTelefono.setText(user.getTelefono());
    etPais.setText(user.getPais());
}

    private void updateProfile() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String apellido = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String pais = etPais.getText() != null ? etPais.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es requerido");
            return;
        }
        if (TextUtils.isEmpty(apellido)) {
            etApellido.setError("El apellido es requerido");
            return;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("El teléfono es requerido");
            return;
        }
        if (TextUtils.isEmpty(pais)) {
            etPais.setError("El país es requerido");
            return;
        }

        showLoading(true);
        btnActualizar.setEnabled(false);

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("nombre", nombre);
        profileData.put("apellido", apellido);
        profileData.put("telefono", telefono);
        profileData.put("pais", pais);
        profileData.put("foto_perfil", ""); // Por ahora vacío

        apiService.updateClientProfile(sessionManager.getAuthHeader(), profileData)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        showLoading(false);
                        btnActualizar.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            User updatedUser = response.body().getData();
                            bindUserData(updatedUser);
                            sessionManager.updateUser(updatedUser);
                            Toast.makeText(requireContext(), "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo actualizar el perfil", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        showLoading(false);
                        btnActualizar.setEnabled(true);
                        Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}