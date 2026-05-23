package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.data.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity responsible for displaying, searching,
 * navigating, and deleting vehicle records.
 */
public class VehicleListActivity extends AppCompatActivity {

    // ---------------------------------------------------------
    // UI COMPONENTS
    // ---------------------------------------------------------

    private ListView vehicleListView;
    private EditText editSearchVehicle;

    private Button btnAddVehicle;
    private Button btnGenerateReport;

    // FUE UI ELEMENTS
    private TextView tvEmptyState;
    private TextView tvVehicleGuide;

    // ---------------------------------------------------------
    // DATA
    // ---------------------------------------------------------

    private final List<Vehicle> vehicles = new ArrayList<>();
    private ArrayAdapter<Vehicle> adapter;
    private VehicleRepository vehicleRepository;

    // ---------------------------------------------------------
    // LIFECYCLE
    // ---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        initializeViews();
        initializeRepository();
        initializeListView();
        initializeListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    // ---------------------------------------------------------
    // INITIALIZATION
    // ---------------------------------------------------------

    private void initializeViews() {

        vehicleListView =
                findViewById(R.id.vehicle_list_view);

        editSearchVehicle =
                findViewById(R.id.editSearchVehicle);

        btnAddVehicle =
                findViewById(R.id.btnAddVehicle);

        btnGenerateReport =
                findViewById(R.id.btnGenerateReport);

        // FUE ELEMENTS
        tvEmptyState =
                findViewById(R.id.tvEmptyState);

        tvVehicleGuide =
                findViewById(R.id.tvVehicleGuide);
    }

    private void initializeRepository() {
        vehicleRepository = new VehicleRepository(this);
    }

    private void initializeListView() {

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                vehicles
        );

        vehicleListView.setAdapter(adapter);
    }

    private void initializeListeners() {

        btnAddVehicle.setOnClickListener(v ->
                openVehicleDetail()
        );

        btnGenerateReport.setOnClickListener(v ->
                openReport()
        );

        vehicleListView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Vehicle selectedVehicle =
                            adapter.getItem(position);

                    if (selectedVehicle != null) {
                        openVehicleDetail(selectedVehicle.getId());
                    }
                }
        );

        vehicleListView.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    Vehicle selectedVehicle =
                            adapter.getItem(position);

                    if (selectedVehicle != null) {
                        showDeleteDialog(selectedVehicle);
                    }

                    return true;
                }
        );

        editSearchVehicle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVehicles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ---------------------------------------------------------
    // LOAD VEHICLES
    // ---------------------------------------------------------

    private void loadVehicles() {

        vehicleRepository.getAllVehicles(
                new VehicleRepository.VehicleCallback() {

                    @Override
                    public void onSuccess(List<Vehicle> result) {

                        runOnUiThread(() -> {

                            vehicles.clear();
                            vehicles.addAll(result);
                            adapter.notifyDataSetChanged();

                            updateEmptyState();
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() ->
                                showToast(error, Toast.LENGTH_LONG)
                        );
                    }
                }
        );
    }

    // ---------------------------------------------------------
    // SEARCH / FILTER
    // ---------------------------------------------------------

    private void filterVehicles(String query) {

        if (query == null || query.trim().isEmpty()) {

            adapter.clear();
            adapter.addAll(vehicles);
            adapter.notifyDataSetChanged();

            updateEmptyState();
            return;
        }

        String normalizedQuery = query.toLowerCase(Locale.US);

        List<Vehicle> filteredVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {

            if (matchesVehicleSearch(vehicle, normalizedQuery)) {
                filteredVehicles.add(vehicle);
            }
        }

        adapter.clear();
        adapter.addAll(filteredVehicles);
        adapter.notifyDataSetChanged();

        updateEmptyState();
    }

    private boolean matchesVehicleSearch(Vehicle vehicle, String query) {

        return containsIgnoreCase(vehicle.getTitle(), query)
                || containsIgnoreCase(vehicle.getMake(), query)
                || containsIgnoreCase(vehicle.getModel(), query)
                || containsIgnoreCase(vehicle.getLocation(), query);
    }

    private boolean containsIgnoreCase(String value, String query) {

        return value != null
                && value.toLowerCase(Locale.US).contains(query);
    }

    // ---------------------------------------------------------
    // EMPTY STATE (FUE)
    // ---------------------------------------------------------

    private void updateEmptyState() {

        if (vehicles.isEmpty()) {

            vehicleListView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);

        } else {

            vehicleListView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    // ---------------------------------------------------------
    // NAVIGATION
    // ---------------------------------------------------------

    private void openVehicleDetail() {

        startActivity(new Intent(
                this,
                VehicleDetailActivity.class
        ));
    }

    private void openVehicleDetail(Long vehicleId) {

        Intent intent =
                new Intent(this, VehicleDetailActivity.class);

        intent.putExtra("vehicleId", vehicleId);
        startActivity(intent);
    }

    private void openReport() {

        startActivity(new Intent(
                this,
                ReportActivity.class
        ));
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    private void showDeleteDialog(Vehicle vehicle) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteVehicle(vehicle)
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteVehicle(Vehicle vehicle) {

        if (vehicle.getId() == null) {

            showToast("Vehicle ID is null", Toast.LENGTH_LONG);
            return;
        }

        vehicleRepository.deleteVehicle(
                vehicle.getId(),
                new VehicleRepository.DeleteVehicleCallback() {

                    @Override
                    public void onSuccess() {

                        runOnUiThread(() -> {

                            showToast("Vehicle deleted", Toast.LENGTH_SHORT);
                            loadVehicles();
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() ->
                                showToast(error, Toast.LENGTH_LONG)
                        );
                    }
                }
        );
    }

    // ---------------------------------------------------------
    // UTILITIES
    // ---------------------------------------------------------

    private void showToast(String message, int duration) {

        Toast.makeText(this, message, duration).show();
    }
}