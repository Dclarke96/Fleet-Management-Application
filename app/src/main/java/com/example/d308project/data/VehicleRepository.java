package com.example.d308project.data;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

public class VehicleRepository {

    private final AppDatabase db;

    public VehicleRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        return db.vehicleDao().getAllVehicles();
    }

    // Get vehicle by ID
    public Vehicle getVehicleById(int id) {
        return db.vehicleDao().getVehicleById(id);
    }

    // Add new vehicle, returns inserted ID or -1 on failure
    public int addVehicle(Vehicle vehicle, Context context) {
        if (vehicle.getMake().isEmpty() || vehicle.getModel().isEmpty()) {
            Toast.makeText(context, "Make and model are required", Toast.LENGTH_SHORT).show();
            return -1;
        }
        long id = db.vehicleDao().insertVehicle(vehicle);
        return (int) id;
    }

    // Update existing vehicle, returns true if successful
    public boolean updateVehicle(Vehicle vehicle, Context context) {
        if (vehicle.getMake().isEmpty() || vehicle.getModel().isEmpty()) {
            Toast.makeText(context, "Make and model are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        db.vehicleDao().updateVehicle(vehicle);
        return true;
    }

    // Delete vehicle (returns false if it has maintenance records)
    public boolean deleteVehicle(Vehicle vehicle, Context context) {
        int count = db.maintenanceDao().countMaintenanceForVehicle(vehicle.getId());
        if (count > 0) {
            Toast.makeText(context, "Cannot delete vehicle with maintenance records", Toast.LENGTH_SHORT).show();
            return false;
        }
        db.vehicleDao().deleteVehicle(vehicle);
        return true;
    }
}
