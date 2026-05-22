package com.dylanclarke.FleetManagementApp.network;

/**
 * Request body used for creating or updating
 * maintenance records through the API.
 */
public class MaintenanceRequest {

    private final long vehicleId;

    private final String description;

    // Service date in yyyy-MM-dd format
    private final String date;

    // Optional maintenance/service cost
    private final Double cost;

    private final boolean alertsEnabled;

    /**
     * Creates a maintenance request payload.
     */
    public MaintenanceRequest(
            long vehicleId,
            String description,
            String date,
            Double cost,
            boolean alertsEnabled
    ) {
        this.vehicleId = vehicleId;
        this.description = description;
        this.date = date;
        this.cost = cost;
        this.alertsEnabled = alertsEnabled;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Double getCost() {
        return cost;
    }

    public boolean isAlertsEnabled() {
        return alertsEnabled;
    }
}