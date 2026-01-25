package com.example.d308project.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface ExcursionDao {

    @Query("SELECT COUNT(*) FROM Excursion WHERE vacationOwnerId = :vacationId")
    int countExcursionsForVacation(int vacationId);

    @Insert
    void insertExcursion(Excursion excursion);

    @Update
    void updateExcursion(Excursion excursion);

    @Delete
    void deleteExcursion(Excursion excursion);

    @Query("DELETE FROM Excursion WHERE id = :excursionId")
    void deleteExcursionById(int excursionId);

    @Query("SELECT * FROM Excursion WHERE vacationOwnerId = :vacationId")
    List<Excursion> getExcursionsForVacation(int vacationId);

    @Query("SELECT * FROM Excursion WHERE id = :id")
    Excursion getExcursionById(int id);
}
