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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.Vehicle;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;

    private VehiclesAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        fabAdd = view.findViewById(R.id.fab_add);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        adapter = new VehiclesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_vehicles_to_addVehicle));

        loadVehicles();
    }

    private void loadVehicles() {
        showLoading(true);
        apiService.getVehicles(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vehicle>>> call, Response<ApiResponse<List<Vehicle>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Vehicle> list = response.body().getData();
                    adapter.setItems(list != null ? list : new ArrayList<>());
                    updateEmptyState();
                } else {
                    Toast.makeText(requireContext(), "No se pudieron cargar los vehículos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vehicle>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private static class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.VehicleVH> {
        private final List<Vehicle> items = new ArrayList<>();

        void setItems(List<Vehicle> data) {
            items.clear();
            items.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VehicleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_simple, parent, false);
            return new VehicleVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleVH holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VehicleVH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle;
            VehicleVH(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            }
            void bind(Vehicle v) {
                tvTitle.setText(v.getDescripcionCorta());
                StringBuilder sb = new StringBuilder();
                sb.append(v.getMarca()).append(" ").append(v.getModelo()).append(" (").append(v.getAnio()).append(")");
                if (v.getTipoAceite() != null && !v.getTipoAceite().isEmpty()) {
                    sb.append(" • ").append(v.getTipoAceite());
                }
                tvSubtitle.setText(sb.toString());
            }
        }
    }
} 