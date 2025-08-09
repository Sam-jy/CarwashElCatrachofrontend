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
import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuotationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabCreate;

    private QuotationsAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quotations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        fabCreate = view.findViewById(R.id.fab_add);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        adapter = new QuotationsAdapter(new OnItemClick() {
            @Override
            public void onClick(Quotation q) {
                Bundle args = new Bundle();
                args.putInt("quotationId", q.getId());
                Navigation.findNavController(requireView()).navigate(R.id.action_quotations_to_quotationDetail, args);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        fabCreate.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_quotations_to_createQuotation));

        loadQuotations();
    }

    private void loadQuotations() {
        showLoading(true);
        apiService.getClientQuotations(sessionManager.getAuthHeader(), null).enqueue(new Callback<ApiResponse<List<Quotation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Quotation>>> call, Response<ApiResponse<List<Quotation>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Quotation> list = response.body().getData();
                    adapter.setItems(list != null ? list : new ArrayList<>());
                    updateEmptyState();
                } else {
                    Toast.makeText(requireContext(), "No se pudieron cargar las cotizaciones", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Quotation>>> call, Throwable t) {
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

    interface OnItemClick {
        void onClick(Quotation q);
    }

    private static class QuotationsAdapter extends RecyclerView.Adapter<QuotationsAdapter.QuotationVH> {
        private final List<Quotation> items = new ArrayList<>();
        private final OnItemClick listener;
        QuotationsAdapter(OnItemClick l) { this.listener = l; }

        void setItems(List<Quotation> data) {
            items.clear();
            items.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public QuotationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quotation_simple, parent, false);
            return new QuotationVH(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull QuotationVH holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class QuotationVH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle, tvStatus;
            Quotation current;
            QuotationVH(@NonNull View itemView, OnItemClick l) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
                tvStatus = itemView.findViewById(R.id.tv_status);
                itemView.setOnClickListener(v -> { if (current != null) l.onClick(current); });
            }
            void bind(Quotation q) {
                current = q;
                tvTitle.setText(q.getServicioNombre() != null ? q.getServicioNombre() : ("Servicio #" + q.getServicioId()));
                tvSubtitle.setText(q.getVehiculoDescripcion() != null ? q.getVehiculoDescripcion() : ("Vehículo #" + q.getVehiculoId()));
                tvStatus.setText(q.getEstadoDisplay());
            }
        }
    }
} 