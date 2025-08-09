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
import androidx.navigation.Navigation;

import com.example.carwashelcatracho.Api.ApiService;
import com.example.carwashelcatracho.Api.RetrofitClient;
import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilNombre, tilApellido, tilEmail, tilTelefono, tilPais, tilPassword, tilConfirmPassword;
    private TextInputEditText etNombre, etApellido, etEmail, etTelefono, etPais, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnGoToLogin;
    private ProgressBar progressBar;

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        apiService = RetrofitClient.getInstance().getApiService();
        setupClickListeners();
    }

    private void initViews(View view) {
        tilNombre = view.findViewById(R.id.til_nombre);
        tilApellido = view.findViewById(R.id.til_apellido);
        tilEmail = view.findViewById(R.id.til_email);
        tilTelefono = view.findViewById(R.id.til_telefono);
        tilPais = view.findViewById(R.id.til_pais);
        tilPassword = view.findViewById(R.id.til_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

        etNombre = view.findViewById(R.id.et_nombre);
        etApellido = view.findViewById(R.id.et_apellido);
        etEmail = view.findViewById(R.id.et_email);
        etTelefono = view.findViewById(R.id.et_telefono);
        etPais = view.findViewById(R.id.et_pais);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        btnRegister = view.findViewById(R.id.btn_register);
        btnGoToLogin = view.findViewById(R.id.btn_go_to_login);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        btnGoToLogin.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void performRegister() {
        String nombre = getText(etNombre);
        String apellido = getText(etApellido);
        String email = getText(etEmail);
        String telefono = getText(etTelefono);
        String pais = getText(etPais);
        String password = getText(etPassword);
        String confirm = getText(etConfirmPassword);

        // Validations
        if (TextUtils.isEmpty(nombre)) { tilNombre.setError("El nombre es requerido"); return; }
        if (TextUtils.isEmpty(apellido)) { tilApellido.setError("El apellido es requerido"); return; }
        if (TextUtils.isEmpty(email)) { tilEmail.setError("El email es requerido"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email inválido"); return; }
        if (TextUtils.isEmpty(telefono)) { tilTelefono.setError("El teléfono es requerido"); return; }
        if (TextUtils.isEmpty(pais)) { tilPais.setError("El país es requerido"); return; }
        if (TextUtils.isEmpty(password)) { tilPassword.setError("La contraseña es requerida"); return; }
        if (password.length() < 6) { tilPassword.setError("Mínimo 6 caracteres"); return; }
        if (!password.equals(confirm)) { tilConfirmPassword.setError("Las contraseñas no coinciden"); return; }

        clearErrors();
        showProgress(true);

        Map<String, String> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("apellido", apellido);
        userData.put("email", email);
        userData.put("telefono", telefono);
        userData.put("password", password);
        userData.put("pais", pais);

        apiService.registerClient(userData).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, Object>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Registro exitoso", Toast.LENGTH_LONG).show();

                        Bundle args = new Bundle();
                        args.putString("email", email);
                        Navigation.findNavController(requireView()).navigate(R.id.action_register_to_emailVerification, args);
                    } else {
                        Toast.makeText(requireContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Error en el registro", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearErrors() {
        tilNombre.setError(null);
        tilApellido.setError(null);
        tilEmail.setError(null);
        tilTelefono.setError(null);
        tilPais.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnGoToLogin.setEnabled(!show);
    }
} 