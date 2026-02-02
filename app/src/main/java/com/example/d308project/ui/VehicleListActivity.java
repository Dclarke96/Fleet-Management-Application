package com.example.d308project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Vehicle;

import java.util.List;

public class VehicleListActivity extends AppCompatActivity {

    private ListView vehicleListView;
    private AppDatabase db;

    private List<Vehicle> vehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        db = AppDatabase.getInstance(getApplicationContext());

        vehicleListView = findViewById(R.id.vehicle_list_view);

        Button btnAddVehicle = findViewById(R.id.btnAddVehicle);
        btnAddVehicle.setOnClickListener(view ->
                startActivity(new Intent(this, VehicleDetailActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVehicles();
    }

    private void loadVehicles() {
        vehicles = db.vehicleDao().getAllVehicles();

        ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                vehicles
        );

        vehicleListView.setAdapter(adapter);

        vehicleListView.setOnItemClickListener((parent, view, position, id) -> {
            Vehicle selectedVehicle = vehicles.get(position);
            Intent intent = new Intent(this, VehicleDetailActivity.class);
            intent.putExtra("vehicleId", selectedVehicle.id);
            startActivity(intent);
        });

        vehicleListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Vehicle selectedVehicle = vehicles.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Vehicle")
                    .setMessage("Are you sure you want to delete this vehicle?")
                    .setPositiveButton("Delete", (dialog, which) ->
                            attemptDeleteVehicle(selectedVehicle))
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
    }

    private void attemptDeleteVehicle(Vehicle vehicle) {
        int maintenanceCount =
                db.maintenanceDao().countMaintenanceForVehicle(vehicle.id);

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
