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
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportsFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView tvSummary, tvStats, tvRatings;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress);
        tvSummary = view.findViewById(R.id.tv_summary);
        tvStats = view.findViewById(R.id.tv_stats);
        tvRatings = view.findViewById(R.id.tv_ratings);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        loadReports();
    }

    private void setLoading(boolean loading) { progressBar.setVisibility(loading ? View.VISIBLE : View.GONE); }

    private void loadReports() {
        setLoading(true);
        apiService.getAdminReports(sessionManager.getAuthHeader(), null, null)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            bindReport(response.body().getData());
                        } else {
                            Toast.makeText(requireContext(), "No se pudo cargar", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void bindReport(Map<String, Object> data) {
        if (data == null) return;

        Map<String, Object> stats = (Map<String, Object>) data.get("estadisticas_cotizaciones");
        if (stats != null) {
            String resumen = "Cotizaciones Total: " + stats.get("total") +
                    "\nPendientes: " + stats.get("pendientes") +
                    "\nEnviadas: " + stats.get("enviadas") +
                    "\nCompletadas: " + stats.get("completadas") +
                    "\nIngresos totales: L. " + stats.get("ingresos_totales");
            tvStats.setText(resumen);
        }

        List<Map<String, Object>> ingresosDia = (List<Map<String, Object>>) data.get("ingresos_por_dia");
        if (ingresosDia != null && !ingresosDia.isEmpty()) {
            Map<String, Object> d = ingresosDia.get(0);
            tvSummary.setText("Ingresos del " + d.get("fecha") + ": L. " + d.get("ingresos_dia") + " (" + d.get("servicios") + " servicios)");
        }

        List<Map<String, Object>> calificaciones = (List<Map<String, Object>>) data.get("calificaciones");
        if (calificaciones != null) {
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> c : calificaciones) {
                sb.append("• ").append(c.get("servicio")).append(": ")
                        .append(c.get("calificacion_promedio")).append(" ⭐ (" )
                        .append(c.get("total_calificaciones")).append(")\n");
            }
            tvRatings.setText(sb.toString());
        }
    }
}


