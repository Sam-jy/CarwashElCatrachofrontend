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
import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuotationDetailFragment extends Fragment {

    private TextView tvServicio, tvVehiculo, tvEstado, tvPrecio;
    private MaterialButton btnAceptar, btnRechazar;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;
    private int quotationId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quotation_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvServicio = view.findViewById(R.id.tv_servicio);
        tvVehiculo = view.findViewById(R.id.tv_vehiculo);
        tvEstado = view.findViewById(R.id.tv_estado);
        tvPrecio = view.findViewById(R.id.tv_precio);
        btnAceptar = view.findViewById(R.id.btn_aceptar);
        btnRechazar = view.findViewById(R.id.btn_rechazar);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            quotationId = getArguments().getInt("quotationId", -1);
        }

        btnAceptar.setOnClickListener(v -> accept());
        btnRechazar.setOnClickListener(v -> reject());

        loadDetail();
    }

    private void loadDetail() {
        showLoading(true);
        apiService.getClientQuotations(sessionManager.getAuthHeader(), null).enqueue(new Callback<ApiResponse<List<Quotation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Quotation>>> call, Response<ApiResponse<List<Quotation>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Quotation found = null;
                    for (Quotation q : response.body().getData()) {
                        if (q.getId() == quotationId) { found = q; break; }
                    }
                    if (found != null) bind(found);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Quotation>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bind(Quotation q) {
        tvServicio.setText(q.getServicioNombre() != null ? q.getServicioNombre() : ("Servicio #" + q.getServicioId()));
        tvVehiculo.setText(q.getVehiculoDescripcion() != null ? q.getVehiculoDescripcion() : ("Vehículo #" + q.getVehiculoId()));
        tvEstado.setText(q.getEstadoDisplay());
        tvPrecio.setText(q.getPrecioFormateado());
        boolean canRespond = q.isEnviada();
        btnAceptar.setEnabled(canRespond);
        btnRechazar.setEnabled(canRespond);
    }

    private void accept() {
        showLoading(true);
        apiService.acceptQuotation(sessionManager.getAuthHeader(), quotationId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), response.body().getMessage() != null ? response.body().getMessage() : "Aceptada", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else { Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show(); }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reject() {
        showLoading(true);
        apiService.rejectQuotation(sessionManager.getAuthHeader(), quotationId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), response.body().getMessage() != null ? response.body().getMessage() : "Rechazada", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else { Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show(); }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAceptar.setEnabled(!show);
        btnRechazar.setEnabled(!show);
    }
} 