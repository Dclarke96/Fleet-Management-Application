package com.dylanclarke.FleetManagementApp.network;

import com.google.gson.annotations.SerializedName;

public class VehicleRequest {

    public String title;
    public String make;
    public String model;
    @SerializedName("vehicleYear")
    public int vehicleYear;
    public String location;
    public boolean maintenanceAlertsEnabled;
    public String startDate;
    public String endDate;

}