package com.example.d308project.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VacationDao {

    @Insert
    long insertVacation(Vacation vacation);

    @Update
    void updateVacation(Vacation vacation);

    @Delete
    void deleteVacation(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY start_date ASC")
    List<Vacation> getAllVacations();

    @Query("SELECT * FROM vacations WHERE id = :id LIMIT 1")
    Vacation getVacationById(int id);

    @Query("DELETE FROM vacations WHERE id = :vacationId")
    void deleteVacationById(int vacationId);

    @Query("SELECT MAX(id) FROM vacations")
    int getLastInsertedId();
}
