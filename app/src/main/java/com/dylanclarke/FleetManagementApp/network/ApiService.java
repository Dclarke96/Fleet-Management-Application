package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.LoginRequest;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST("/api/auth/login")
    Call<ApiResponse<String>> login(@Body LoginRequest request);

    @GET("/api/vehicles")
    Call<List<Vehicle>> getVehicles();

    @POST("/api/vehicles")
    Call<Vehicle> addVehicle(@Body Vehicle vehicle);

    @DELETE("/api/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") Long id);
}