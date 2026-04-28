package com.dylanclarke.FleetManagementApp.network;

import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.LoginRequest;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse<String>> login(@Body LoginRequest request);

    @GET("vehicles")
    Call<ApiResponse<List<Vehicle>>> getVehicles(@Header("Authorization") String token);
}