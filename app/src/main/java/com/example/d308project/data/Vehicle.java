package com.example.d308project.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "vehicles")
public class Vehicle {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "make")
    public String make;

    @ColumnInfo(name = "model")
    public String model;

    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "license_plate")
    public String licensePlate;

    @ColumnInfo(name = "maintenance_alerts_enabled")
    public boolean maintenanceAlertsEnabled;

    @ColumnInfo(name = "start_date")
    public String startDate; // yyyy-MM-dd

    @ColumnInfo(name = "end_date")
    public String endDate;   // yyyy-MM-dd, can be null if vehicle is still active

    public Vehicle() {}

    public Vehicle(String make, String model, int year, String licensePlate,
                   boolean maintenanceAlertsEnabled, String startDate, String endDate) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public String toString() {
        return year + " " + make + " " + model + " (" + licensePlate + ")" +
                " [" + startDate + " - " + (endDate != null ? endDate : "Present") + "]";
    }
}
