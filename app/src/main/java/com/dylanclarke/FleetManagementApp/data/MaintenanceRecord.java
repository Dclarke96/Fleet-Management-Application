package com.dylanclarke.FleetManagementApp.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a maintenance/service record associated with a vehicle.
 */
public class MaintenanceRecord extends BaseEntity {

    @NonNull
    @SerializedName("description")
    private String description = "";

    // Service date returned from API (currently stored as String)
    @NonNull
    @SerializedName("date")
    private String serviceDate = "";

    @SerializedName("alertsEnabled")
    private boolean alertsEnabled;

    // Parent vehicle ID associated with this maintenance record
    @SerializedName("vehicleId")
    private int vehicleId;

    // Optional service cost
    @SerializedName("cost")
    private Double cost;

    /**
     * Required empty constructor for Gson deserialization.
     */
    public MaintenanceRecord() {
    }

    public MaintenanceRecord(
            @NonNull String description,
            @NonNull String serviceDate,
            Double cost,
            boolean alertsEnabled,
            int vehicleId
    ) {
        this.description = description;
        this.serviceDate = serviceDate;
        this.cost = cost;
        this.alertsEnabled = alertsEnabled;
        this.vehicleId = vehicleId;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(@NonNull String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public boolean isAlertsEnabled() {
        return alertsEnabled;
    }

    public void setAlertsEnabled(boolean alertsEnabled) {
        this.alertsEnabled = alertsEnabled;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    /**
     * Returns the primary UI display value for this record.
     */
    @NonNull
    @Override
    public String displayName() {
        return description;
    }

    /**
     * Returns a readable summary for logging and list displays.
     */
    @NonNull
    @Override
    public String toString() {
        return serviceDate + ": " + description +
                (alertsEnabled ? " [Alert]" : "");
    }
}