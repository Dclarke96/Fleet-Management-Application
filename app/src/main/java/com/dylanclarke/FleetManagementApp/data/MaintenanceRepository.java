package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// SCALABILITY DESIGN:
// Repository pattern separates UI from data layer.
// Allows future expansion to APIs, caching,
// background sync, or alternative data sources
// without modifying Activities.
public class MaintenanceRepository {

    private final AppDatabase db;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public MaintenanceRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    // Get maintenance records
    public List<MaintenanceRecord> getMaintenanceForVehicle(int vehicleId) {
        return db.maintenanceDao().getMaintenanceForVehicle(vehicleId);
    }

    public List<MaintenanceRecord> getAllMaintenance() {
        return db.maintenanceDao().getAllMaintenance();
    }

    public MaintenanceRecord getMaintenanceById(int id) {
        return db.maintenanceDao().getMaintenanceById(id);
    }

    // Add maintenance record
    public boolean addMaintenance(MaintenanceRecord record, Context context) {
        if (!validateRecord(record, context)) return false;
        db.maintenanceDao().insertMaintenance(record);
        return true;
    }

    // Update maintenance record
    public boolean updateMaintenance(MaintenanceRecord record, Context context) {
        if (!validateRecord(record, context)) return false;
        db.maintenanceDao().updateMaintenance(record);
        return true;
    }

    // Delete maintenance record
    public void deleteMaintenance(MaintenanceRecord record) {
        db.maintenanceDao().deleteMaintenance(record);
    }

    public void deleteMaintenanceById(int id) {
        db.maintenanceDao().deleteMaintenanceById(id);
    }

    public Vehicle getVehicleFor(int vehicleId) {
        return db.vehicleDao().getVehicleById(vehicleId);
    }

    // -----------------------------------------------------------
    // SECURITY: Centralized Input Validation
    // Ensures:
    // - Required fields are present
    // - Date formats are valid
    // - Service dates fall within allowed vehicle ranges
    // - Related vehicle exists before saving maintenance
    // Prevents invalid data entry and protects database integrity
    // -----------------------------------------------------------

    // --------- Validation ---------
    private boolean validateRecord(MaintenanceRecord record, Context context) {

        // Required fields
        if (record.getDescription() == null || record.getDescription().trim().isEmpty() ||
                record.getServiceDate() == null || record.getServiceDate().trim().isEmpty()) {

            Toast.makeText(context, "Description and service date are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Minimum description length
        if (record.getDescription().length() < 3) {
            Toast.makeText(context, "Description must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate service date format
        Date serviceDate;
        try {
            sdf.setLenient(false);
            serviceDate = sdf.parse(record.getServiceDate());
        } catch (ParseException e) {
            Toast.makeText(context, "Invalid date format (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Prevent past dates
        if (serviceDate.before(new Date())) {
            Toast.makeText(context, "Service date cannot be in the past", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Vehicle must exist
        Vehicle vehicle = db.vehicleDao().getVehicleById(record.getVehicleId());
        if (vehicle == null) {
            Toast.makeText(context, "Associated vehicle not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Date startDate = sdf.parse(vehicle.getStartDate());

            Date endDate = null;
            if (vehicle.getEndDate() != null && !vehicle.getEndDate().isEmpty()) {
                endDate = sdf.parse(vehicle.getEndDate());
            }

            if (serviceDate.before(startDate)) {
                Toast.makeText(context, "Service date cannot be before vehicle start date", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (endDate != null && serviceDate.after(endDate)) {
                Toast.makeText(context, "Service date cannot be after vehicle end date", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (ParseException ignored) {}

        return true;
    }
}
