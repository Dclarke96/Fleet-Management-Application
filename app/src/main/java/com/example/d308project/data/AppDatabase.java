package com.example.d308project.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {Vehicle.class, MaintenanceRecord.class},
        version = 7
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract VehicleDao vehicleDao();
    public abstract MaintenanceDao maintenanceDao();

    // SECURITY NOTE:
    // Using Room ORM provides parameterized queries
    // which protects against SQL injection attacks.
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "fleet_management_db"
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
