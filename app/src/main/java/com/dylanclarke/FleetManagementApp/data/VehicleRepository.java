package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    // Search vehicles
    public List<Vehicle> searchVehicles(String query) {
        return db.vehicleDao().searchVehicles(query);
    }

    // Add new vehicle
    public int addVehicle(Vehicle vehicle) {
        String validationError = validateVehicle(vehicle);
        if (validationError != null) return -1;

        long id = db.vehicleDao().insertVehicle(vehicle);
        return (int) id;
    }

    // Update existing vehicle
    public boolean updateVehicle(Vehicle vehicle) {
        String validationError = validateVehicle(vehicle);
        if (validationError != null) return false;

        db.vehicleDao().updateVehicle(vehicle);
        return true;
    }

    // Delete vehicle
    public boolean deleteVehicle(Vehicle vehicle) {
        int count = db.maintenanceDao().countMaintenanceForVehicle(vehicle.getId());
        if (count > 0) {
            return false;
        }

        db.vehicleDao().deleteVehicle(vehicle);
        return true;
    }

    // Public validation helper for UI layer
    public String getValidationError(Vehicle vehicle) {
        return validateVehicle(vehicle);
    }

    // -----------------------------------------------------------
    // SECURITY: Repository-Level Validation
    // -----------------------------------------------------------
    private String validateVehicle(Vehicle vehicle) {

        if (vehicle.getMake().isEmpty() ||
                vehicle.getModel().isEmpty() ||
                vehicle.getLocation().isEmpty()) {
            return "Make, model, and location are required";
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (vehicle.getYear() < 1900 || vehicle.getYear() > currentYear) {
            return "Year must be between 1900 and " + currentYear;
        }

        try {
            sdf.setLenient(false);
            sdf.parse(vehicle.getStartDate());

            if (vehicle.getEndDate() != null &&
                    !vehicle.getEndDate().isEmpty()) {

                if (sdf.parse(vehicle.getEndDate())
                        .before(sdf.parse(vehicle.getStartDate()))) {
                    return "End date cannot be before start date";
                }
            }

        } catch (ParseException e) {
            return "Invalid date format (yyyy-MM-dd)";
        }

        return null;
    }
}
