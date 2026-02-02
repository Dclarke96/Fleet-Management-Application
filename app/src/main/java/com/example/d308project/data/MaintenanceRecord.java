package com.example.d308project.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "maintenance_records",
        foreignKeys = @ForeignKey(
                entity = Vehicle.class,
                parentColumns = "id",
                childColumns = "vehicle_id", // <-- must match @ColumnInfo below
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("vehicle_id")} // <-- same here
)
public class MaintenanceRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "description")
    public String description = "";

    @NonNull
    @ColumnInfo(name = "service_date")
    public String serviceDate = "";

    @ColumnInfo(name = "alerts_enabled")
    public boolean alertsEnabled;

    @ColumnInfo(name = "vehicle_id") // <-- must match childColumns in @ForeignKey
    public int vehicleId;

    public MaintenanceRecord() {
    }

    public MaintenanceRecord(
            @NonNull String description,
            @NonNull String serviceDate,
            boolean alertsEnabled,
            int vehicleId
    ) {
        this.description = description;
        this.serviceDate = serviceDate;
        this.alertsEnabled = alertsEnabled;
        this.vehicleId = vehicleId;
    }
}
