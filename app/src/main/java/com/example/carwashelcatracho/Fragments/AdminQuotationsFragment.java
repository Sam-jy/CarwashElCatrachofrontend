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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminQuotationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    private ApiService apiService;
    private SessionManager sessionManager;
    private QuotationsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_quotations, container, false);
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

        adapter = new QuotationsAdapter(new ArrayList<>(), new QuotationsAdapter.OnItemActionListener() {
            @Override
            public void onRespond(Quotation quotation) { showRespondDialog(quotation); }

            @Override
            public void onComplete(Quotation quotation) { showCompleteDialog(quotation); }

            @Override
            public void onCancel(Quotation quotation) { cancelQuotation(quotation.getId()); }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadPending);

        loadPending();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void loadPending() {
        setLoading(true);
        apiService.getPendingQuotations(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<List<Quotation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Quotation>>> call, Response<ApiResponse<List<Quotation>>> response) {
                setLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Quotation> data = response.body().getData();
                    adapter.setItems(data);
                    tvEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "No se pudo cargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Quotation>>> call, Throwable t) {
                setLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRespondDialog(Quotation quotation) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_admin_respond, null, false);
        final com.google.android.material.textfield.TextInputEditText etPrecio = dialogView.findViewById(R.id.et_precio);
        final com.google.android.material.textfield.TextInputEditText etNotas = dialogView.findViewById(R.id.et_notas);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Responder cotización")
                .setView(dialogView)
                .setPositiveButton("Enviar", (d, w) -> {
                    String precioStr = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";
                    String notas = etNotas.getText() != null ? etNotas.getText().toString().trim() : null;
                    if (TextUtils.isEmpty(precioStr)) {
                        Toast.makeText(requireContext(), "Precio requerido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        double precio = Double.parseDouble(precioStr);
                        respondQuotation(quotation.getId(), precio, notas);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void respondQuotation(int quotationId, double precio, String notas) {
        setLoading(true);
        Map<String, Object> body = new HashMap<>();
        body.put("precio", precio);
        if (!TextUtils.isEmpty(notas)) body.put("notas_admin", notas);

        apiService.respondToQuotation(sessionManager.getAuthHeader(), quotationId, body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Cotización enviada al cliente", Toast.LENGTH_SHORT).show();
                            loadPending();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo responder", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showCompleteDialog(Quotation quotation) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_admin_complete, null, false);
        final com.google.android.material.textfield.TextInputEditText etObservaciones = dialogView.findViewById(R.id.et_observaciones);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Completar servicio")
                .setMessage("Confirmar que el servicio fue completado")
                .setView(dialogView)
                .setPositiveButton("Completar", (d, w) -> {
                    String obs = etObservaciones.getText() != null ? etObservaciones.getText().toString().trim() : null;
                    completeService(quotation.getId(), obs);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void completeService(int quotationId, String observaciones) {
        setLoading(true);
        Map<String, Object> body = new HashMap<>();
        if (!TextUtils.isEmpty(observaciones)) body.put("observaciones", observaciones);

        apiService.completeService(sessionManager.getAuthHeader(), quotationId, body)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Servicio completado", Toast.LENGTH_SHORT).show();
                            loadPending();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo completar", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cancelQuotation(int quotationId) {
        setLoading(true);
        apiService.cancelQuotation(sessionManager.getAuthHeader(), quotationId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Cotización cancelada", Toast.LENGTH_SHORT).show();
                            loadPending();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo cancelar", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Adapter interno simple para mostrar pendientes con acciones
    static class QuotationsAdapter extends RecyclerView.Adapter<QuotationsAdapter.QVH> {
        interface OnItemActionListener {
            void onRespond(Quotation quotation);
            void onComplete(Quotation quotation);
            void onCancel(Quotation quotation);
        }

        private List<Quotation> items;
        private final OnItemActionListener listener;

        QuotationsAdapter(List<Quotation> items, OnItemActionListener listener) {
            this.items = items;
            this.listener = listener;
        }

        void setItems(List<Quotation> newItems) {
            this.items = newItems != null ? newItems : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public QVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quotation_admin, parent, false);
            return new QVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull QVH holder, int position) {
            holder.bind(items.get(position), listener);
        }

        @Override
        public int getItemCount() { return items != null ? items.size() : 0; }

        static class QVH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle, tvStatus;
            View btnRespond, btnComplete, btnCancel;
            QVH(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
                tvStatus = itemView.findViewById(R.id.tv_status);
                btnRespond = itemView.findViewById(R.id.btn_respond);
                btnComplete = itemView.findViewById(R.id.btn_complete);
                btnCancel = itemView.findViewById(R.id.btn_cancel);
            }
            void bind(Quotation q, OnItemActionListener l) {
                tvTitle.setText(q.getServicioNombre() + " • " + q.getPrecioFormateado());
                tvSubtitle.setText(q.getClienteNombre() + " • " + q.getVehiculoDescripcion());
                tvStatus.setText(q.getEstadoDisplay());
                btnRespond.setOnClickListener(v -> l.onRespond(q));
                btnComplete.setOnClickListener(v -> l.onComplete(q));
                btnCancel.setOnClickListener(v -> l.onCancel(q));
            }
        }
    }
}


