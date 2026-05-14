package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("/api/auth/login")
    Call<ApiResponse<String>> login(@Body LoginRequest request);

    @GET("/api/vehicles")
    Call<ApiResponse<PageResponse<Vehicle>>> getVehicles();

    @POST("/api/vehicles")
    Call<ApiResponse<Vehicle>> addVehicle(@Body VehicleRequest request);

    @GET("/api/vehicles/{id}")
    Call<ApiResponse<Vehicle>> getVehicleById(@Path("id") Long id);

    @PUT("/api/vehicles/{id}")
    Call<ApiResponse<Vehicle>> updateVehicle(@Path("id") long id, @Body VehicleRequest request);

    @DELETE("/api/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") Long id);

    @GET("/api/maintenance")
    Call<ApiResponse<List<MaintenanceRecord>>> getMaintenance();

    @GET("/api/maintenance/vehicle/{vehicleId}")
    Call<ApiResponse<List<MaintenanceRecord>>> getMaintenanceForVehicle(@Path("vehicleId") long vehicleId);

    @POST("/api/maintenance")
    Call<ApiResponse<MaintenanceRecord>> addMaintenance(@Body MaintenanceRequest request);

    @PUT("/api/maintenance/{id}")
    Call<ApiResponse<MaintenanceRecord>> updateMaintenance(@Path("id") long id, @Body MaintenanceRequest request);

    @DELETE("/api/maintenance/{id}")
    Call<Void> deleteMaintenance(@Path("id") long id);
}