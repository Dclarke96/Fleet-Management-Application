package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.PageResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleRepository {

    private final AppDatabase db;
    private final ApiService apiService;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public VehicleRepository(Context context) {

        db = AppDatabase.getInstance(context);

        apiService = ApiClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ---------------------------------------------------------
    // API VERSION - Get all vehicles
    // ---------------------------------------------------------
    public void getAllVehicles(VehicleCallback callback) {

        apiService.getVehicles().enqueue(
                new Callback<ApiResponse<PageResponse<Vehicle>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<Vehicle>>> call,
                            Response<ApiResponse<PageResponse<Vehicle>>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(
                                    response.body()
                                            .getData()
                                            .content
                            );

                        } else {

                            callback.onError(
                                    "Failed to load vehicles"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<Vehicle>>> call,
                            Throwable t
                    ) {

                        callback.onError(
                                t.getMessage()
                        );
                    }
                }
        );
    }

    public List<Vehicle> getAllVehiclesLocal() {
        return db.vehicleDao().getAllVehicles();
    }

    // ---------------------------------------------------------
    // Get vehicle by ID (still Room for now)
    // ---------------------------------------------------------
    public Vehicle getVehicleById(int id) {
        return db.vehicleDao().getVehicleById(id);
    }

    // ---------------------------------------------------------
    // Search vehicles (still Room for now)
    // ---------------------------------------------------------
    public List<Vehicle> searchVehicles(String query) {
        return db.vehicleDao().searchVehicles(query);
    }

    // ---------------------------------------------------------
    // Add new vehicle (still Room for now)
    // ---------------------------------------------------------
    public int addVehicle(Vehicle vehicle) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) return -1;

        long id = db.vehicleDao().insertVehicle(vehicle);

        return (int) id;
    }

    // ---------------------------------------------------------
    // Update existing vehicle (still Room for now)
    // ---------------------------------------------------------
    public boolean updateVehicle(Vehicle vehicle) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) return false;

        db.vehicleDao().updateVehicle(vehicle);

        return true;
    }

    // ---------------------------------------------------------
    // Delete vehicle (still mixed for now)
    // ---------------------------------------------------------
    public boolean deleteVehicle(Vehicle vehicle) {

        int count = db.maintenanceDao()
                .countMaintenanceForVehicle(
                        vehicle.getId().intValue()
                );

        if (count > 0) {
            return false;
        }

        db.vehicleDao().deleteVehicle(vehicle);

        return true;
    }

    // ---------------------------------------------------------
    // Public validation helper
    // ---------------------------------------------------------
    public String getValidationError(Vehicle vehicle) {
        return validateVehicle(vehicle);
    }

    // ---------------------------------------------------------
    // Callback Interface
    // ---------------------------------------------------------
    public interface VehicleCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String error);
    }

    // ---------------------------------------------------------
    // SECURITY: Repository-Level Validation
    // ---------------------------------------------------------
    private String validateVehicle(Vehicle vehicle) {

        if (vehicle.getMake().isEmpty() ||
                vehicle.getModel().isEmpty() ||
                vehicle.getLocation().isEmpty()) {

            return "Make, model, and location are required";
        }

        int currentYear =
                Calendar.getInstance().get(Calendar.YEAR);

        if (vehicle.getYear() < 1900
                || vehicle.getYear() > currentYear) {

            return "Year must be between 1900 and "
                    + currentYear;
        }

        try {

            sdf.setLenient(false);

            sdf.parse(vehicle.getStartDate());

            if (vehicle.getEndDate() != null
                    && !vehicle.getEndDate().isEmpty()) {

                if (sdf.parse(vehicle.getEndDate())
                        .before(
                                sdf.parse(
                                        vehicle.getStartDate()
                                )
                        )) {

                    return "End date cannot be before start date";
                }
            }

        } catch (ParseException e) {

            return "Invalid date format (yyyy-MM-dd)";
        }

        return null;
    }
}