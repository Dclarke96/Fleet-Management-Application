package com.dylanclarke.FleetManagementApp.network;

import com.google.gson.annotations.SerializedName;

/**
 * Request body used for creating or updating vehicles
 * through the backend API.
 */
public class VehicleRequest {

    private final String title;

    private final String make;

    private final String model;

    @SerializedName("vehicleYear")
    private final int vehicleYear;

    private final String location;

    private final boolean maintenanceAlertsEnabled;

    // Vehicle ownership/use start date (yyyy-MM-dd)
    private final String startDate;

    // Optional end date (yyyy-MM-dd)
    private final String endDate;

    /**
     * Creates a vehicle API request payload.
     */
    public VehicleRequest(
            String title,
            String make,
            String model,
            int vehicleYear,
            String location,
            boolean maintenanceAlertsEnabled,
            String startDate,
            String endDate
    ) {
        this.title = title;
        this.make = make;
        this.model = model;
        this.vehicleYear = vehicleYear;
        this.location = location;
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public String getLocation() {
        return location;
    }

    public boolean isMaintenanceAlertsEnabled() {
        return maintenanceAlertsEnabled;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}