package com.example.d308project.data;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


// SCALABILITY DESIGN:
// Repository pattern separates UI from data layer.
// Allows future expansion to APIs, caching,
// background sync, or alternative data sources
// without modifying Activities.
public class VehicleRepository {

    private final AppDatabase db;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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
        if (!validateVehicle(vehicle, context)) return -1;
        long id = db.vehicleDao().insertVehicle(vehicle);
        return (int) id;
    }

    // Update existing vehicle, returns true if successful
    public boolean updateVehicle(Vehicle vehicle, Context context) {
        if (!validateVehicle(vehicle, context)) return false;
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

    // -----------------------------------------------------------
    // SECURITY: Repository-Level Validation
    // Prevents:
    // - Empty or malformed vehicle data
    // - Invalid dates
    // - Data integrity issues before DB insertion
    // Acts as secure gatekeeper between UI and database
    // -----------------------------------------------------------

    // --------- Validation ---------
    private boolean validateVehicle(Vehicle vehicle, Context context) {
        // Check required fields
        if (vehicle.getMake().isEmpty() || vehicle.getModel().isEmpty() || vehicle.getLocation().isEmpty()) {
            Toast.makeText(context, "Make, model, and location are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check date formats
        try {
            sdf.setLenient(false);
            sdf.parse(vehicle.getStartDate());

            if (vehicle.getEndDate() != null && !vehicle.getEndDate().isEmpty()) {
                if (sdf.parse(vehicle.getEndDate()).before(sdf.parse(vehicle.getStartDate()))) {
                    Toast.makeText(context, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (ParseException e) {
            Toast.makeText(context, "Invalid date format (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
