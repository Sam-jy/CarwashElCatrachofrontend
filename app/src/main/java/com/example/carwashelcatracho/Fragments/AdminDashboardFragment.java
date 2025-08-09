package com.example.carwashelcatracho.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class AdminDashboardFragment extends Fragment {

    private TextView tvTitle;
    private ProgressBar progressBar;

    // Usuarios
    private TextView tvUsuariosClientes, tvUsuariosAdmins, tvUsuariosVerificadosClientes, tvUsuariosVerificadosAdmins;
    // Cotizaciones
    private TextView tvCotTotal, tvCotPendientes, tvCotEnviadas, tvCotAceptadas, tvCotCompletadas, tvCotRechazadas, tvCotCanceladas, tvCotPrecioPromedio, tvCotIngresosTotales;
    // Servicios
    private TextView tvServiciosTotal, tvServiciosDomicilio, tvServiciosCentro, tvServiciosPrecioPromedio;
    // Ingresos mes
    private TextView tvIngresosMesTotal, tvIngresosMesServicios;
    // Listas
    private LinearLayout listServiciosPopulares, listClientesFrecuentes;

    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getInstance().getApiService();

        if (sessionManager.getUser() != null) {
            tvTitle.setText("Bienvenido, " + sessionManager.getUser().getNombreCompleto());
        }

        loadDashboard();
    }

    private void bindViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        progressBar = view.findViewById(R.id.progress_bar);

        tvUsuariosClientes = view.findViewById(R.id.tv_usuarios_clientes);
        tvUsuariosAdmins = view.findViewById(R.id.tv_usuarios_admins);
        tvUsuariosVerificadosClientes = view.findViewById(R.id.tv_usuarios_verificados_clientes);
        tvUsuariosVerificadosAdmins = view.findViewById(R.id.tv_usuarios_verificados_admins);

        tvCotTotal = view.findViewById(R.id.tv_cot_total);
        tvCotPendientes = view.findViewById(R.id.tv_cot_pendientes);
        tvCotEnviadas = view.findViewById(R.id.tv_cot_enviadas);
        tvCotAceptadas = view.findViewById(R.id.tv_cot_aceptadas);
        tvCotCompletadas = view.findViewById(R.id.tv_cot_completadas);
        tvCotRechazadas = view.findViewById(R.id.tv_cot_rechazadas);
        tvCotCanceladas = view.findViewById(R.id.tv_cot_canceladas);
        tvCotPrecioPromedio = view.findViewById(R.id.tv_cot_precio_promedio);
        tvCotIngresosTotales = view.findViewById(R.id.tv_cot_ingresos_totales);

        tvServiciosTotal = view.findViewById(R.id.tv_serv_total);
        tvServiciosDomicilio = view.findViewById(R.id.tv_serv_domicilio);
        tvServiciosCentro = view.findViewById(R.id.tv_serv_centro);
        tvServiciosPrecioPromedio = view.findViewById(R.id.tv_serv_precio_promedio);

        tvIngresosMesTotal = view.findViewById(R.id.tv_ingresos_mes_total);
        tvIngresosMesServicios = view.findViewById(R.id.tv_ingresos_mes_servicios);

        listServiciosPopulares = view.findViewById(R.id.list_servicios_populares);
        listClientesFrecuentes = view.findViewById(R.id.list_clientes_frecuentes);
    }

    private void loadDashboard() {
        showLoading(true);
        apiService.getAdminDashboard(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    bindDashboard(response.body().getData());
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar el dashboard", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void bindDashboard(Map<String, Object> data) {
        if (data == null) return;

        // Usuarios
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) data.get("usuarios");
        int totalClientes = 0, totalAdmins = 0, verificadosClientes = 0, verificadosAdmins = 0;
        if (usuarios != null) {
            for (Map<String, Object> u : usuarios) {
                String tipo = str(u.get("tipo_usuario"));
                int total = intVal(u.get("total"));
                int verificados = intVal(u.get("verificados"));
                if ("cliente".equalsIgnoreCase(tipo)) { totalClientes = total; verificadosClientes = verificados; }
                if ("admin".equalsIgnoreCase(tipo)) { totalAdmins = total; verificadosAdmins = verificados; }
            }
        }
        tvUsuariosClientes.setText(String.valueOf(totalClientes));
        tvUsuariosAdmins.setText(String.valueOf(totalAdmins));
        tvUsuariosVerificadosClientes.setText(String.valueOf(verificadosClientes));
        tvUsuariosVerificadosAdmins.setText(String.valueOf(verificadosAdmins));

        // Cotizaciones
        Map<String, Object> cot = (Map<String, Object>) data.get("cotizaciones");
        if (cot != null) {
            tvCotTotal.setText(String.valueOf(intVal(cot.get("total"))));
            tvCotPendientes.setText(String.valueOf(intVal(cot.get("pendientes"))));
            tvCotEnviadas.setText(String.valueOf(intVal(cot.get("enviadas"))));
            tvCotAceptadas.setText(String.valueOf(intVal(cot.get("aceptadas"))));
            tvCotCompletadas.setText(String.valueOf(intVal(cot.get("completadas"))));
            tvCotRechazadas.setText(String.valueOf(intVal(cot.get("rechazadas"))));
            tvCotCanceladas.setText(String.valueOf(intVal(cot.get("canceladas"))));
            tvCotPrecioPromedio.setText(formatMoney(cot.get("precio_promedio")));
            tvCotIngresosTotales.setText(formatMoney(cot.get("ingresos_totales")));
        }

        // Servicios
        Map<String, Object> serv = (Map<String, Object>) data.get("servicios");
        if (serv != null) {
            tvServiciosTotal.setText(String.valueOf(intVal(serv.get("total_servicios"))));
            tvServiciosDomicilio.setText(String.valueOf(intVal(serv.get("disponibles_domicilio"))));
            tvServiciosCentro.setText(String.valueOf(intVal(serv.get("disponibles_centro"))));
            tvServiciosPrecioPromedio.setText(formatMoney(serv.get("precio_promedio")));
        }

        // Ingresos mes
        Map<String, Object> ingresosMes = (Map<String, Object>) data.get("ingresos_mes");
        if (ingresosMes != null) {
            tvIngresosMesTotal.setText(formatMoney(ingresosMes.get("total")));
            tvIngresosMesServicios.setText(String.valueOf(intVal(ingresosMes.get("servicios"))));
        }

        // Listas
        listServiciosPopulares.removeAllViews();
        List<Map<String, Object>> serviciosPopulares = (List<Map<String, Object>>) data.get("servicios_populares");
        if (serviciosPopulares != null) {
            for (Map<String, Object> sp : serviciosPopulares) {
                TextView item = buildListItem(
                        str(sp.get("nombre")) + " • " +
                        str(sp.get("total_servicios")) + " servicios • Prom. " + formatMoney(sp.get("precio_promedio")) +
                        (sp.get("calificacion_promedio") != null ? (" • ⭐ " + str(sp.get("calificacion_promedio"))) : "")
                );
                listServiciosPopulares.addView(item);
            }
        }

        listClientesFrecuentes.removeAllViews();
        List<Map<String, Object>> clientesFrecuentes = (List<Map<String, Object>>) data.get("clientes_frecuentes");
        if (clientesFrecuentes != null) {
            for (Map<String, Object> cf : clientesFrecuentes) {
                String nombre = str(cf.get("nombre")) + " " + str(cf.get("apellido"));
                String linea = nombre + " • " + str(cf.get("total_servicios")) + " servicios • Gasto " + formatMoney(cf.get("total_gastado"));
                TextView item = buildListItem(linea);
                listClientesFrecuentes.addView(item);
            }
        }
    }

    private TextView buildListItem(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.text_primary));
        tv.setTextSize(14);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        tv.setPadding(0, pad, 0, pad);
        return tv;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String str(Object o) { return o == null ? "" : String.valueOf(o); }
    private int intVal(Object o) {
        try {
            if (o == null) return 0;
            if (o instanceof Number) return ((Number) o).intValue();
            return (int) Math.round(Double.parseDouble(String.valueOf(o)));
        } catch (Exception e) { return 0; }
    }
    private String formatMoney(Object o) {
        try {
            double v = 0.0;
            if (o instanceof Number) v = ((Number) o).doubleValue();
            else if (o != null) v = Double.parseDouble(String.valueOf(o));
            return "L. " + String.format("%.2f", v);
        } catch (Exception e) { return "L. 0.00"; }
    }
} 