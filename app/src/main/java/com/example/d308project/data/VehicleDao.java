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
    long insertVacation(Vehicle vehicle);

    @Update
    void updateVacation(Vehicle vehicle);

    @Delete
    void deleteVacation(Vehicle vehicle);

    @Query("SELECT * FROM Vehicle ORDER BY start_date ASC")
    List<Vehicle> getAllVacations();

    @Query("SELECT * FROM Vehicle WHERE id = :id LIMIT 1")
    Vehicle getVacationById(int id);

    @Query("DELETE FROM Vehicle WHERE id = :vacationId")
    void deleteVacationById(int vacationId);

    @Query("SELECT MAX(id) FROM Vehicle")
    int getLastInsertedId();
}
