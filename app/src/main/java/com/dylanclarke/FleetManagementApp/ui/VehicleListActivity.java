package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.AppDatabase;
import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class VehicleListActivity extends AppCompatActivity {

    private ListView vehicleListView;
    private EditText editSearchVehicle;
    private List<Vehicle> vehicles = new ArrayList<>();
    private ArrayAdapter<Vehicle> adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        vehicleListView = findViewById(R.id.vehicle_list_view);
        editSearchVehicle = findViewById(R.id.editSearchVehicle);
        Button btnAddVehicle = findViewById(R.id.btnAddVehicle);
        Button btnGenerateReport = findViewById(R.id.btnGenerateReport);

        btnGenerateReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        });

        btnAddVehicle.setOnClickListener(view ->
                startActivity(new Intent(this, VehicleDetailActivity.class))
        );

        // Initialize adapter once (better performance + stability)
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                vehicles
        );

        vehicleListView.setAdapter(adapter);

        vehicleListView.setOnItemClickListener((parent, view, position, id) -> {
            Vehicle selectedVehicle = adapter.getItem(position);
            if (selectedVehicle != null) {
                Intent intent = new Intent(this, VehicleDetailActivity.class);
                intent.putExtra("vehicleId", selectedVehicle.getId());
                startActivity(intent);
            }
        });

        vehicleListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Vehicle selectedVehicle = adapter.getItem(position);
            if (selectedVehicle != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Vehicle")
                        .setMessage("Are you sure you want to delete this vehicle?")
                        .setPositiveButton("Delete", (dialog, which) ->
                                deleteVehicle(selectedVehicle))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });

        // Live incremental search
        editSearchVehicle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    private void loadVehicles() {

        apiService.getVehicles().enqueue(new Callback<List<Vehicle>>() {

            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    vehicles.clear();
                    vehicles.addAll(response.body());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(VehicleListActivity.this,
                            "Failed to load vehicles",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {

                Toast.makeText(VehicleListActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterVehicles(String query) {

        if (query == null || query.trim().isEmpty()) {
            loadVehicles();
            return;
        }

        String lower = query.toLowerCase();

        List<Vehicle> filtered = new ArrayList<>();

        for (Vehicle v : vehicles) {
            if (v.getTitle().toLowerCase().contains(lower) ||
                    v.getMake().toLowerCase().contains(lower) ||
                    v.getModel().toLowerCase().contains(lower) ||
                    v.getLocation().toLowerCase().contains(lower)) {
                filtered.add(v);
            }
        }

        adapter.clear();
        adapter.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    private void deleteVehicle(Vehicle vehicle) {

        apiService.deleteVehicle(vehicle.getId()).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(VehicleListActivity.this,
                            "Vehicle deleted",
                            Toast.LENGTH_SHORT).show();
                    loadVehicles();
                } else {
                    Toast.makeText(VehicleListActivity.this,
                            "Delete failed: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VehicleListActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
