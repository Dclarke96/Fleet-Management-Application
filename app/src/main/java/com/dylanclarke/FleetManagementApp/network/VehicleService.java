package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleService {

    @GET("/api/vehicles")
    Call<List<Vehicle>> getAllVehicles();

    @GET("/api/vehicles/{id}")
    Call<Vehicle> getVehicle(@Path("id") Long id);

    @POST("/api/vehicles")
    Call<Vehicle> createVehicle(@Body Vehicle vehicle);

    @PUT("/api/vehicles/{id}")
    Call<Vehicle> updateVehicle(@Path("id") Long id, @Body Vehicle vehicle);

    @DELETE("/api/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") Long id);
}