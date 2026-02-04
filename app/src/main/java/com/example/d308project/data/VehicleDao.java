package com.example.d308project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VehicleDao {

    @Insert
    long insertVehicle(Vehicle vehicle);

    @Update
    void updateVehicle(Vehicle vehicle);

    @Delete
    void deleteVehicle(Vehicle vehicle);

    @Query("SELECT * FROM vehicles ORDER BY year ASC")
    List<Vehicle> getAllVehicles();

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    Vehicle getVehicleById(int id);

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    void deleteVehicleById(int vehicleId);

    @Query("SELECT MAX(id) FROM vehicles")
    int getLastInsertedId();

    // ✅ Search query for multiple fields (make, model, license plate)
    @Query("SELECT * FROM vehicles " +
            "WHERE make LIKE :query OR model LIKE :query OR license_plate LIKE :query " +
            "ORDER BY make ASC, model ASC")
    List<Vehicle> searchVehicles(String query);
}
