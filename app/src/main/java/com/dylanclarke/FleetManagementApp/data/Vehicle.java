package com.dylanclarke.FleetManagementApp.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a vehicle managed within the fleet system.
 */
public class Vehicle extends BaseEntity {

    private String title;

    private String make;

    private String model;

    @SerializedName("vehicleYear")
    private int year;

    private String location;

    private boolean maintenanceAlertsEnabled;

    // Active service/rental period start date
    private String startDate;

    // Optional service/rental end date
    private String endDate;

    /**
     * Required empty constructor for Gson deserialization.
     */
    public Vehicle() {
    }

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

    // ---------------------------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isMaintenanceAlertsEnabled() {
        return maintenanceAlertsEnabled;
    }

    public void setMaintenanceAlertsEnabled(
            boolean maintenanceAlertsEnabled
    ) {
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the primary display name used throughout the UI.
     */
    @NonNull
    @Override
    public String displayName() {

        StringBuilder builder = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            builder.append(title).append(" - ");
        }

        builder.append(make).append(" ").append(model);

        return builder.toString();
    }

    /**
     * Returns a readable summary for logging and list displays.
     */
    @NonNull
    @Override
    public String toString() {

        String displayEndDate =
                (endDate != null && !endDate.isEmpty())
                        ? endDate
                        : "Present";

        return displayName()
                + " (" + location + ")"
                + " [" + startDate + " - "
                + displayEndDate + "]";
    }
}