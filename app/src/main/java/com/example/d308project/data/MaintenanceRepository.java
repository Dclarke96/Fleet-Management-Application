package com.example.d308project.data;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MaintenanceRepository {

    private final AppDatabase db;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public MaintenanceRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public List<MaintenanceRecord> getMaintenanceForVehicle(int vehicleId) {
        return db.maintenanceDao().getMaintenanceForVehicle(vehicleId);
    }

    public MaintenanceRecord getMaintenanceById(int id) {
        return db.maintenanceDao().getMaintenanceById(id);
    }

    public boolean addMaintenance(MaintenanceRecord record, Context context) {
        if (!validateRecord(record, context)) return false;
        db.maintenanceDao().insertMaintenance(record);
        return true;
    }

    public boolean updateMaintenance(MaintenanceRecord record, Context context) {
        if (!validateRecord(record, context)) return false;
        db.maintenanceDao().updateMaintenance(record);
        return true;
    }

    public void deleteMaintenance(MaintenanceRecord record) {
        db.maintenanceDao().deleteMaintenance(record);
    }

    public void deleteMaintenanceById(int id) {
        db.maintenanceDao().deleteMaintenanceById(id);
    }

    public Vehicle getVehicleFor(int vehicleId) {
        return db.vehicleDao().getVehicleById(vehicleId);
    }

    private boolean validateRecord(MaintenanceRecord record, Context context) {
        if (record.getDescription().isEmpty() || record.getServiceDate().isEmpty()) {
            Toast.makeText(context, "Description and date are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Ensure date format is valid
        try {
            sdf.setLenient(false);
            sdf.parse(record.getServiceDate());
        } catch (ParseException e) {
            Toast.makeText(context, "Invalid date format (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            return false;
        }

        Vehicle vehicle = db.vehicleDao().getVehicleById(record.getVehicleId());
        if (vehicle == null) {
            Toast.makeText(context, "Parent vehicle not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            if (sdf.parse(record.getServiceDate()).before(sdf.parse(vehicle.getStartDate()))) {
                Toast.makeText(context, "Maintenance date cannot be before vehicle start date", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (vehicle.getEndDate() != null && !vehicle.getEndDate().isEmpty()) {
                if (sdf.parse(record.getServiceDate()).after(sdf.parse(vehicle.getEndDate()))) {
                    Toast.makeText(context, "Maintenance date cannot be after vehicle end date", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (ParseException ignored) {}

        return true;
    }
}
