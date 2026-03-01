package com.dylanclarke.FleetManagementApp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VehicleDao {

    // ---------------------------------------------------------
    // CREATE
    // Insert a new vehicle into the database
    // ---------------------------------------------------------
    @Insert
    long insertVehicle(Vehicle vehicle);

    // ---------------------------------------------------------
    // UPDATE
    // Updates all vehicle fields including:
    // title, make, model, year, location, alerts, dates
    // ---------------------------------------------------------
    @Update
    void updateVehicle(Vehicle vehicle);

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Delete
    void deleteVehicle(Vehicle vehicle);

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    void deleteVehicleById(int vehicleId);

    // ---------------------------------------------------------
    // READ
    // ---------------------------------------------------------
    @Query("SELECT * FROM vehicles ORDER BY year ASC, make ASC, model ASC")
    List<Vehicle> getAllVehicles();

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    Vehicle getVehicleById(int id);

    @Query("SELECT MAX(id) FROM vehicles")
    int getLastInsertedId();

    // ---------------------------------------------------------
    // SEARCH FUNCTIONALITY (WGU PART B REQUIREMENT)
    //
    // Supports:
    // - Partial matches
    // - Multiple results
    // - Title / Make / Model / Location fields
    //
    // Example Matches:
    // B → Blue Subi
    // Sub → Blue Subi
    // Gar → Garage A
    // ---------------------------------------------------------
    @Query("SELECT * FROM vehicles " +
            "WHERE title LIKE '%' || :query || '%' " +
            "OR make LIKE '%' || :query || '%' " +
            "OR model LIKE '%' || :query || '%' " +
            "OR location LIKE '%' || :query || '%' " +
            "ORDER BY year ASC, make ASC, model ASC")
    List<Vehicle> searchVehicles(String query);
}
