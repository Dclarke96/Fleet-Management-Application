package com.dylanclarke.FleetManagementApp.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

@Entity(
        tableName = "maintenance_records",
        foreignKeys = @ForeignKey(
                entity = Vehicle.class,
                parentColumns = "id",
                childColumns = "vehicle_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("vehicle_id")}
)
public class MaintenanceRecord extends BaseEntity {

    @NonNull
    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String description = "";

    @NonNull
    @ColumnInfo(name = "service_date")
    @SerializedName("date") // 🔥 FIX: backend sends "date"
    private String serviceDate = "";

    @ColumnInfo(name = "alerts_enabled")
    @SerializedName("alertsEnabled")
    private boolean alertsEnabled;

    @ColumnInfo(name = "vehicle_id")
    @SerializedName("vehicleId")
    private int vehicleId;

    @ColumnInfo(name = "cost")
    @SerializedName("cost")
    private Double cost; // 🔥 FIX: allow null safely

    public MaintenanceRecord() {}

    public MaintenanceRecord(@NonNull String description,
                             @NonNull String serviceDate,
                             Double cost,
                             boolean alertsEnabled,
                             int vehicleId) {

        this.description = description;
        this.serviceDate = serviceDate;
        this.cost = cost;
        this.alertsEnabled = alertsEnabled;
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
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

    @NonNull
    @Override
    public String displayName() {
        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return serviceDate + ": " + description + (alertsEnabled ? " [Alert]" : "");
    }
}