package com.dylanclarke.FleetManagementApp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MaintenanceDao {

    // Count maintenance records for a specific vehicle
    @Query("SELECT COUNT(*) FROM maintenance_records WHERE vehicle_id = :vehicleId")
    int countMaintenanceForVehicle(int vehicleId);

    @Insert
    void insertMaintenance(MaintenanceRecord maintenanceRecord);

    @Query("SELECT * FROM maintenance_records")
    List<MaintenanceRecord> getAllMaintenance();

    @Update
    void updateMaintenance(MaintenanceRecord maintenanceRecord);

    @Delete
    void deleteMaintenance(MaintenanceRecord maintenanceRecord);

    @Query("DELETE FROM maintenance_records WHERE id = :maintenanceId")
    void deleteMaintenanceById(int maintenanceId);

    // Retrieve all maintenance records for a vehicle
    @Query("SELECT * FROM maintenance_records WHERE vehicle_id = :vehicleId ORDER BY service_date ASC")
    List<MaintenanceRecord> getMaintenanceForVehicle(int vehicleId);

    @Query("SELECT * FROM maintenance_records WHERE id = :id LIMIT 1")
    MaintenanceRecord getMaintenanceById(int id);
}
