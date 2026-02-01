package com.example.d308project.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface MaintenanceDao {

    @Query("SELECT COUNT(*) FROM MaintenanceRecord WHERE vacationOwnerId = :vacationId")
    int countExcursionsForVacation(int vacationId);

    @Insert
    void insertExcursion(MaintenanceRecord maintenanceRecord);

    @Update
    void updateExcursion(MaintenanceRecord maintenanceRecord);

    @Delete
    void deleteExcursion(MaintenanceRecord maintenanceRecord);

    @Query("DELETE FROM MaintenanceRecord WHERE id = :excursionId")
    void deleteExcursionById(int excursionId);

    @Query("SELECT * FROM MaintenanceRecord WHERE vacationOwnerId = :vacationId")
    List<MaintenanceRecord> getExcursionsForVacation(int vacationId);

    @Query("SELECT * FROM MaintenanceRecord WHERE id = :id")
    MaintenanceRecord getExcursionById(int id);
}
