package com.dylanclarke.FleetManagementApp.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "vehicles")
public class Vehicle extends BaseEntity {

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "make")
    public String make;

    @ColumnInfo(name = "model")
    public String model;

    // 🔥 FIX: Match backend DTO field name EXACTLY
    @SerializedName("vehicleYear")
    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "maintenance_alerts_enabled")
    public boolean maintenanceAlertsEnabled;

    @ColumnInfo(name = "start_date")
    public String startDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

    public Vehicle() {}

    public Vehicle(
            String title,
            String make,
            String model,
            int year,
            String location,
            boolean maintenanceAlertsEnabled,
            String startDate,
            String endDate
    ) {
        this.title = title;
        this.make = make;
        this.model = model;
        this.year = year;
        this.location = location;
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public String toString() {
        return (title != null && !title.isEmpty() ? title + " - " : "") +
                year + " " + make + " " + model + " (" + location + ")" +
                " [" + startDate + " - " +
                (endDate != null && !endDate.isEmpty() ? endDate : "Present") + "]";
    }

    // BaseEntity ID
    public Long getId() {
        return id;
    }

    // Getters / Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isMaintenanceAlertsEnabled() {
        return maintenanceAlertsEnabled;
    }

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
        return (title != null && !title.isEmpty() ? title + " - " : "")
                + make + " " + model;
    }
}