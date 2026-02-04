package com.example.d308project.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "vehicles")
public class Vehicle extends BaseEntity {

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
    public String endDate;   // yyyy-MM-dd, nullable if still active

    public Vehicle() {}

    public Vehicle(
            String make,
            String model,
            int year,
            String licensePlate,
            boolean maintenanceAlertsEnabled,
            String startDate,
            String endDate
    ) {
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

    // ✅ Getter for ID (protected in BaseEntity)
    public int getId() {
        return id;
    }

    // Optional: getters/setters for other fields
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public boolean isMaintenanceAlertsEnabled() { return maintenanceAlertsEnabled; }
    public void setMaintenanceAlertsEnabled(boolean maintenanceAlertsEnabled) {
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
    }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @NonNull
    @Override
    public String displayName() {
        return make + " " + model;
    }
}
