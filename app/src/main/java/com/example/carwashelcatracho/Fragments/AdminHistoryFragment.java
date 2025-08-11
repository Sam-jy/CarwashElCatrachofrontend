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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    private ApiService apiService;
    private SessionManager sessionManager;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        tvEmpty = view.findViewById(R.id.tv_empty);
        progressBar = view.findViewById(R.id.progress);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        adapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadHistory);

        loadHistory();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void loadHistory() {
        setLoading(true);
        apiService.getAdminHistory(sessionManager.getAuthHeader(), null, null, null, null, null)
                .enqueue(new Callback<ApiResponse<List<Map<String, Object>>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Map<String, Object>>>> call, Response<ApiResponse<List<Map<String, Object>>>> response) {
                        setLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            // Aprovechamos el HistoryAdapter que recibe Quotation; mapeamos campos necesarios
                            List<Map<String, Object>> raw = response.body().getData();
                            List<Quotation> items = new ArrayList<>();
                            if (raw != null) {
                                for (Map<String, Object> r : raw) {
                                    Quotation q = new Quotation();
                                    q.setServicioNombre(String.valueOf(r.get("servicio_nombre")));
                                    q.setMarca(String.valueOf(r.get("marca")));
                                    q.setModelo(String.valueOf(r.get("modelo")));
                                    q.setPlaca(String.valueOf(r.get("placa")));
                                    q.setFechaServicio(String.valueOf(r.get("fecha_servicio")));
                                    try { q.setPrecioCotizado(Double.parseDouble(String.valueOf(r.get("precio_final")))); } catch (Exception ignored) {}
                                    q.setEstado("completada");
                                    items.add(q);
                                }
                            }
                            adapter.setItems(items);
                            tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext(), "No se pudo cargar", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Map<String, Object>>>> call, Throwable t) {
                        setLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        tvEmpty.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}


