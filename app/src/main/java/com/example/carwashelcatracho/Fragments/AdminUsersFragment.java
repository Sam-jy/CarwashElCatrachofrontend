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
import com.example.carwashelcatracho.Models.User;
import com.example.carwashelcatracho.R;
import com.example.carwashelcatracho.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    private ApiService apiService;
    private SessionManager sessionManager;
    private UsersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
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

        adapter = new UsersAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);

        loadUsers();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void loadUsers() {
        setLoading(true);
        apiService.getUsers(sessionManager.getAuthHeader(), null).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                setLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<User> data = response.body().getData();
                    adapter.setItems(data);
                    tvEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "No se pudo cargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                setLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UVH> {
        private List<User> items;
        UsersAdapter(List<User> items) { this.items = items; }
        void setItems(List<User> newItems) { this.items = newItems != null ? newItems : new ArrayList<>(); notifyDataSetChanged(); }
        @NonNull
        @Override
        public UVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
            return new UVH(v);
        }
        @Override
        public void onBindViewHolder(@NonNull UVH holder, int position) { holder.bind(items.get(position)); }
        @Override
        public int getItemCount() { return items != null ? items.size() : 0; }
        static class UVH extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvPhone;
            UVH(@NonNull View itemView) { super(itemView); tvName = itemView.findViewById(R.id.tv_name); tvEmail = itemView.findViewById(R.id.tv_email); tvPhone = itemView.findViewById(R.id.tv_phone); }
            void bind(User u) { tvName.setText(u.getNombre() + " " + u.getApellido()); tvEmail.setText(u.getEmail()); tvPhone.setText(u.getTelefono()); }
        }
    }
}


