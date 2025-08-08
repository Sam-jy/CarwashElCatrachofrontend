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

public class EmailVerificationFragment extends Fragment {

    private TextView tvEmailLabel;
    private TextInputLayout tilCode;
    private TextInputEditText etCode;
    private MaterialButton btnVerify, btnResend;
    private ProgressBar progressBar;

    private ApiService apiService;
    private String emailArg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        apiService = RetrofitClient.getInstance().getApiService();

        if (getArguments() != null) {
            emailArg = getArguments().getString("email");
        }
        if (emailArg != null) {
            tvEmailLabel.setText(emailArg);
        }

        setupClickListeners();
    }

    private void initViews(View view) {
        tvEmailLabel = view.findViewById(R.id.tv_email_value);
        tilCode = view.findViewById(R.id.til_code);
        etCode = view.findViewById(R.id.et_code);
        btnVerify = view.findViewById(R.id.btn_verify);
        btnResend = view.findViewById(R.id.btn_resend);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnVerify.setOnClickListener(v -> verifyCode());
        btnResend.setOnClickListener(v -> resendCode());
    }

    private void verifyCode() {
        String code = etCode.getText() != null ? etCode.getText().toString().trim() : "";
        if (TextUtils.isEmpty(code)) {
            tilCode.setError("Ingresa el código");
            return;
        }
        tilCode.setError(null);
        showProgress(true);

        Map<String, String> data = new HashMap<>();
        data.put("email", emailArg);
        data.put("codigo", code);

        apiService.verifyEmail(data).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Email verificado", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(requireView()).navigate(R.id.loginClientFragment);
                    } else {
                        Toast.makeText(requireContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Código inválido", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendCode() {
        showProgress(true);

        Map<String, String> data = new HashMap<>();
        data.put("email", emailArg);

        apiService.resendCode(data).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    Toast.makeText(requireContext(), apiResponse.getMessage() != null ? apiResponse.getMessage() : "Código reenviado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Error en el servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnVerify.setEnabled(!show);
        btnResend.setEnabled(!show);
    }
} 