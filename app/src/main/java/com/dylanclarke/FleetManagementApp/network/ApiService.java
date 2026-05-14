package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.Vehicle;

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

    @DELETE("/api/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") Long id);
}