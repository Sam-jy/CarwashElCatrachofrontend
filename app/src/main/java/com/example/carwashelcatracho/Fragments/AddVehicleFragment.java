package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.Vehicle;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleFragment extends Fragment {

    private TextInputLayout tilMarca, tilModelo, tilAnio, tilPlaca, tilAceite, tilColor;
    private TextInputEditText etMarca, etModelo, etAnio, etPlaca, etAceite, etColor;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tilMarca = view.findViewById(R.id.til_marca);
        tilModelo = view.findViewById(R.id.til_modelo);
        tilAnio = view.findViewById(R.id.til_anio);
        tilPlaca = view.findViewById(R.id.til_placa);
        tilAceite = view.findViewById(R.id.til_aceite);
        tilColor = view.findViewById(R.id.til_color);
        etMarca = view.findViewById(R.id.et_marca);
        etModelo = view.findViewById(R.id.et_modelo);
        etAnio = view.findViewById(R.id.et_anio);
        etPlaca = view.findViewById(R.id.et_placa);
        etAceite = view.findViewById(R.id.et_aceite);
        etColor = view.findViewById(R.id.et_color);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        btnSave.setOnClickListener(v -> performSave());
    }

    private void performSave() {
        String marca = textOf(etMarca);
        String modelo = textOf(etModelo);
        String anioStr = textOf(etAnio);
        String placa = textOf(etPlaca);
        String aceite = textOf(etAceite);
        String color = textOf(etColor);

        if (TextUtils.isEmpty(marca)) { tilMarca.setError("Marca requerida"); return; }
        if (TextUtils.isEmpty(modelo)) { tilModelo.setError("Modelo requerido"); return; }
        if (TextUtils.isEmpty(anioStr)) { tilAnio.setError("Año requerido"); return; }
        if (TextUtils.isEmpty(placa)) { tilPlaca.setError("Placa requerida"); return; }
        tilMarca.setError(null); tilModelo.setError(null); tilAnio.setError(null); tilPlaca.setError(null);

        int anio;
        try { anio = Integer.parseInt(anioStr); } catch (NumberFormatException e) { tilAnio.setError("Año inválido"); return; }

        showLoading(true);
        Map<String, Object> body = new HashMap<>();
        body.put("marca", marca);
        body.put("modelo", modelo);
        body.put("anio", anio);
        body.put("placa", placa);
        if (!TextUtils.isEmpty(aceite)) body.put("tipo_aceite", aceite);
        if (!TextUtils.isEmpty(color)) body.put("color", color);

        apiService.createVehicle(sessionManager.getAuthHeader(), body).enqueue(new Callback<ApiResponse<Vehicle>>() {
            @Override
            public void onResponse(Call<ApiResponse<Vehicle>> call, Response<ApiResponse<Vehicle>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Vehicle> api = response.body();
                    if (api.isSuccess()) {
                        Toast.makeText(requireContext(), api.getMessage() != null ? api.getMessage() : "Vehículo creado", Toast.LENGTH_LONG).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), api.getMessage() != null ? api.getMessage() : "Error al crear", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Vehicle>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String textOf(TextInputEditText e) { return e.getText() != null ? e.getText().toString().trim() : ""; }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }
} 