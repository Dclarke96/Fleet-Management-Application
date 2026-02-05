package com.example.d308project.ui;

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

import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Vehicle;

import java.util.List;

// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class VehicleListActivity extends AppCompatActivity {

    private ListView vehicleListView;
    private EditText editSearchVehicle;
    private AppDatabase db;
    private List<Vehicle> vehicles;
    private ArrayAdapter<Vehicle> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        db = AppDatabase.getInstance(getApplicationContext());

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

        // Listen for search input
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
        vehicles = db.vehicleDao().getAllVehicles();

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
                                attemptDeleteVehicle(selectedVehicle))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });
    }

    private void filterVehicles(String query) {
        String searchTerm = "%" + query.trim() + "%";
        vehicles = db.vehicleDao().searchVehicles(searchTerm);
        adapter.clear();
        adapter.addAll(vehicles);
        adapter.notifyDataSetChanged();
    }

    private void attemptDeleteVehicle(Vehicle vehicle) {
        int maintenanceCount =
                db.maintenanceDao().countMaintenanceForVehicle(vehicle.getId());

        if (maintenanceCount > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Vehicle")
                    .setMessage("This vehicle has maintenance records. Delete them first.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            db.vehicleDao().deleteVehicle(vehicle);
            loadVehicles();
            Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
