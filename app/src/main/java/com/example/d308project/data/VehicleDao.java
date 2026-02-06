package com.example.d308project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VehicleDao {

    // Insert a new vehicle into the database
    @Insert
    long insertVehicle(Vehicle vehicle);

    // Update an existing vehicle; all fields, including make, model, year, location, are updated
    @Update
    void updateVehicle(Vehicle vehicle);

    // Delete a vehicle from the database
    @Delete
    void deleteVehicle(Vehicle vehicle);

    // Retrieve all vehicles, ordered by year ascending, then make/model for readability
    @Query("SELECT * FROM vehicles ORDER BY year ASC, make ASC, model ASC")
    List<Vehicle> getAllVehicles();

    // Retrieve a single vehicle by its ID
    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    Vehicle getVehicleById(int id);

    // Delete a vehicle by ID (alternative to @Delete)
    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    void deleteVehicleById(int vehicleId);

    // Get the last inserted ID (useful for repository operations)
    @Query("SELECT MAX(id) FROM vehicles")
    int getLastInsertedId();

    // Search vehicles by make, model, or location; supports partial matches
    @Query("SELECT * FROM vehicles " +
            "WHERE make LIKE :query OR model LIKE :query OR location LIKE :query " +
            "ORDER BY year ASC, make ASC, model ASC")
    List<Vehicle> searchVehicles(String query);
}
