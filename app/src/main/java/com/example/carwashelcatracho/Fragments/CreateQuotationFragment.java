package com.example.carwashelcatracho.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.Service;
import com.example.carwashelcatracho.Models.Vehicle;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateQuotationFragment extends Fragment {

    private Spinner spVehicle, spService, spUbicacion;
    private TextInputLayout tilDireccion, tilFecha, tilHora, tilNotas;
    private TextInputEditText etDireccion, etFecha, etHora, etNotas;
    private MaterialButton btnFecha, btnHora, btnCreate;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Service> services = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_quotation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spVehicle = view.findViewById(R.id.sp_vehicle);
        spService = view.findViewById(R.id.sp_service);
        spUbicacion = view.findViewById(R.id.sp_ubicacion);
        tilDireccion = view.findViewById(R.id.til_direccion);
        tilFecha = view.findViewById(R.id.til_fecha);
        tilHora = view.findViewById(R.id.til_hora);
        tilNotas = view.findViewById(R.id.til_notas);
        etDireccion = view.findViewById(R.id.et_direccion);
        etFecha = view.findViewById(R.id.et_fecha);
        etHora = view.findViewById(R.id.et_hora);
        etNotas = view.findViewById(R.id.et_notas);
        btnFecha = view.findViewById(R.id.btn_fecha);
        btnHora = view.findViewById(R.id.btn_hora);
        btnCreate = view.findViewById(R.id.btn_create);
        progressBar = view.findViewById(R.id.progress_bar);

        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(requireContext());

        setupUbicacionSpinner();
        setupPickers();
        btnCreate.setOnClickListener(v -> performCreate());

        loadVehiclesAndServices();
    }

    private void setupUbicacionSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"centro", "domicilio"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUbicacion.setAdapter(adapter);
    }

    private void setupPickers() {
        btnFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog d = new DatePickerDialog(requireContext(), (view, y, m, day) -> {
                String mm = String.format("%02d", m + 1);
                String dd = String.format("%02d", day);
                etFecha.setText(y + "-" + mm + "-" + dd);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            d.show();
        });

        btnHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            TimePickerDialog t = new TimePickerDialog(requireContext(), (view, h, m) -> {
                String hh = String.format("%02d", h);
                String mm = String.format("%02d", m);
                etHora.setText(hh + ":" + mm + ":00");
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
            t.show();
        });
    }

    private void loadVehiclesAndServices() {
        showLoading(true);
        // Load vehicles
        apiService.getVehicles(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<List<Vehicle>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vehicle>>> call, Response<ApiResponse<List<Vehicle>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    vehicles = response.body().getData();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, buildVehicleLabels(vehicles));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spVehicle.setAdapter(adapter);
                }
                // Regardless, then load services
                apiService.getServices(sessionManager.getAuthHeader()).enqueue(new Callback<ApiResponse<List<Service>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Service>>> call, Response<ApiResponse<List<Service>>> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            services = response.body().getData();
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, buildServiceLabels(services));
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spService.setAdapter(adapter);
                        } else {
                            Toast.makeText(requireContext(), "No se pudieron cargar servicios", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Service>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vehicle>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<String> buildVehicleLabels(List<Vehicle> list) {
        List<String> labels = new ArrayList<>();
        if (list != null) for (Vehicle v : list) labels.add(v.getDescripcionCorta());
        return labels;
    }

    private List<String> buildServiceLabels(List<Service> list) {
        List<String> labels = new ArrayList<>();
        if (list != null) for (Service s : list) labels.add(s.getNombre());
        return labels;
    }

    private void performCreate() {
        if (vehicles == null || vehicles.isEmpty()) { Toast.makeText(requireContext(), "Agrega un vehículo primero", Toast.LENGTH_LONG).show(); return; }
        if (services == null || services.isEmpty()) { Toast.makeText(requireContext(), "No hay servicios disponibles", Toast.LENGTH_LONG).show(); return; }

        int vehicleIndex = spVehicle.getSelectedItemPosition();
        int serviceIndex = spService.getSelectedItemPosition();
        String ubicacion = spUbicacion.getSelectedItem().toString();
        String fecha = textOf(etFecha);
        String hora = textOf(etHora);
        String direccion = textOf(etDireccion);
        String notas = textOf(etNotas);

        // Validaciones
        if (TextUtils.isEmpty(fecha)) { tilFecha.setError("Selecciona una fecha"); return; }
        if (TextUtils.isEmpty(hora)) { tilHora.setError("Selecciona una hora"); return; }
        tilFecha.setError(null);
        tilHora.setError(null);

        Service selected = services.get(serviceIndex);
        if ("domicilio".equals(ubicacion) && !selected.isDisponibleDomicilio()) {
            Toast.makeText(requireContext(), "Este servicio no está disponible a domicilio", Toast.LENGTH_LONG).show();
            return;
        }
        if (selected.isCambioAceite() && "domicilio".equals(ubicacion)) {
            Toast.makeText(requireContext(), "El cambio de aceite solo se realiza en centro", Toast.LENGTH_LONG).show();
            return;
        }

        if ("domicilio".equals(ubicacion) && TextUtils.isEmpty(direccion)) {
            tilDireccion.setError("Ingresa una dirección para el servicio a domicilio");
            return;
        }
        tilDireccion.setError(null);

        showLoading(true);
        Map<String, Object> body = new HashMap<>();
        body.put("vehiculo_id", vehicles.get(vehicleIndex).getId());
        body.put("servicio_id", services.get(serviceIndex).getId());
        body.put("tipo_ubicacion", ubicacion);
        body.put("direccion_servicio", TextUtils.isEmpty(direccion) ? null : direccion);
        body.put("fecha_servicio", fecha);
        body.put("hora_servicio", hora);
        if (!TextUtils.isEmpty(notas)) body.put("notas_cliente", notas);

        apiService.createQuotation(sessionManager.getAuthHeader(), body).enqueue(new Callback<ApiResponse<com.example.carwashelcatracho.Models.Quotation>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.carwashelcatracho.Models.Quotation>> call, Response<ApiResponse<com.example.carwashelcatracho.Models.Quotation>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<?> api = response.body();
                    if (api.isSuccess()) {
                        Toast.makeText(requireContext(), api.getMessage() != null ? api.getMessage() : "Cotización creada", Toast.LENGTH_LONG).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), api.getMessage() != null ? api.getMessage() : "Error al crear cotización", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.carwashelcatracho.Models.Quotation>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String textOf(TextInputEditText e) { return e.getText() != null ? e.getText().toString().trim() : ""; }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCreate.setEnabled(!show);
        btnFecha.setEnabled(!show);
        btnHora.setEnabled(!show);
    }
} 