package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.Vehicle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit API definitions for Fleet Management backend.
 * Handles vehicle, maintenance, and authentication endpoints.
 */
public interface ApiService {

    // ---------------------------------------------------------
    // AUTH
    // ---------------------------------------------------------

    @POST("/api/auth/login")
    Call<ApiResponse<String>> login(
            @Body LoginRequest request
    );

    // ---------------------------------------------------------
    // VEHICLES
    // ---------------------------------------------------------

    @GET("/api/vehicles")
    Call<ApiResponse<PageResponse<Vehicle>>> getVehicles();

    @GET("/api/vehicles/{id}")
    Call<ApiResponse<Vehicle>> getVehicleById(
            @Path("id") Long id
    );

    @POST("/api/vehicles")
    Call<ApiResponse<Vehicle>> addVehicle(
            @Body VehicleRequest request
    );

    @PUT("/api/vehicles/{id}")
    Call<ApiResponse<Vehicle>> updateVehicle(
            @Path("id") long id,
            @Body VehicleRequest request
    );

    @DELETE("/api/vehicles/{id}")
    Call<Void> deleteVehicle(
            @Path("id") Long id
    );

    // ---------------------------------------------------------
    // MAINTENANCE
    // ---------------------------------------------------------

    @GET("/api/maintenance")
    Call<ApiResponse<PageResponse<MaintenanceRecord>>> getMaintenance();

    @GET("/api/maintenance/{id}")
    Call<ApiResponse<MaintenanceRecord>> getMaintenanceById(
            @Path("id") long id
    );

    @GET("/api/maintenance/vehicle/{vehicleId}")
    Call<ApiResponse<PageResponse<MaintenanceRecord>>> getMaintenanceForVehicle(
            @Path("vehicleId") long vehicleId
    );

    @POST("/api/maintenance")
    Call<ApiResponse<MaintenanceRecord>> addMaintenance(
            @Body MaintenanceRequest request
    );

    @PUT("/api/maintenance/{id}")
    Call<ApiResponse<MaintenanceRecord>> updateMaintenance(
            @Path("id") long id,
            @Body MaintenanceRequest request
    );

    @DELETE("/api/maintenance/{id}")
    Call<Void> deleteMaintenance(
            @Path("id") long id
    );
}