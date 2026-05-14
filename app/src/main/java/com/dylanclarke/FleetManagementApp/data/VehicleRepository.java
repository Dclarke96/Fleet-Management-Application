package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.PageResponse;
import com.dylanclarke.FleetManagementApp.network.VehicleRequest;

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
                                    "Failed to load vehicles (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<Vehicle>>> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "Network error: " + t.getMessage()
                        );
                    }
                }
        );
    }

    // ---------------------------------------------------------
    // LOCAL FALLBACK (TEMP - still used in tests)
    // ---------------------------------------------------------
    public List<Vehicle> getAllVehiclesLocal() {
        return db.vehicleDao().getAllVehicles();
    }

    // ---------------------------------------------------------
    // API GET VEHICLE BY ID
    // ---------------------------------------------------------
    public void getVehicleById(Long vehicleId, SingleVehicleCallback callback) {

        apiService.getVehicleById(vehicleId)
                .enqueue(new Callback<ApiResponse<Vehicle>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<Vehicle>> call,
                            Response<ApiResponse<Vehicle>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(
                                    response.body().getData()
                            );

                        } else {

                            callback.onError(
                                    "Failed to load vehicle"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Vehicle>> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "Network error: " + t.getMessage()
                        );
                    }
                });
    }

    // ---------------------------------------------------------
    // Get vehicle by ID (Room)
    // ---------------------------------------------------------
    public Vehicle getVehicleById(int id) {
        return db.vehicleDao().getVehicleById(id);
    }

    // ---------------------------------------------------------
    // Search vehicles (Room)
    // ---------------------------------------------------------
    public List<Vehicle> searchVehicles(String query) {
        return db.vehicleDao().searchVehicles(query);
    }

    // ---------------------------------------------------------
    // API ADD VEHICLE (ASYNC)
    // ---------------------------------------------------------
    public void addVehicle(Vehicle vehicle, AddVehicleCallback callback) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        VehicleRequest request = new VehicleRequest();

        request.title = vehicle.getTitle();
        request.make = vehicle.getMake();
        request.model = vehicle.getModel();

        // IMPORTANT:
        // Backend expects "vehicleYear"
        request.vehicleYear = vehicle.getYear();

        request.location = vehicle.getLocation();
        request.maintenanceAlertsEnabled =
                vehicle.isMaintenanceAlertsEnabled();

        request.startDate = vehicle.getStartDate();
        request.endDate = vehicle.getEndDate();

        apiService.addVehicle(request)
                .enqueue(new Callback<ApiResponse<Vehicle>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<Vehicle>> call,
                            Response<ApiResponse<Vehicle>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(
                                    response.body().getData()
                            );

                        } else {

                            String errorBody = "";

                            try {

                                if (response.errorBody() != null) {
                                    errorBody =
                                            response.errorBody().string();
                                }

                            } catch (Exception ignored) {}

                            callback.onError(
                                    "Server error: HTTP "
                                            + response.code()
                                            + " "
                                            + errorBody
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Vehicle>> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "Network error: " + t.getMessage()
                        );
                    }
                });
    }

    // ---------------------------------------------------------
    // LOCAL VERSION (used for tests)
    // ---------------------------------------------------------
    public int addVehicle(Vehicle vehicle) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) return -1;

        long id = db.vehicleDao().insertVehicle(vehicle);

        return (int) id;
    }

    // ---------------------------------------------------------
    // Update (Room for now)
    // ---------------------------------------------------------
    public boolean updateVehicle(Vehicle vehicle) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) return false;

        db.vehicleDao().updateVehicle(vehicle);

        return true;
    }

    // ---------------------------------------------------------
    // API DELETE VEHICLE BY ID
    // ---------------------------------------------------------
    public void deleteVehicle(long vehicleId, DeleteVehicleCallback callback) {

        apiService.deleteVehicle(vehicleId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        if (response.isSuccessful()) {

                            callback.onSuccess();

                        } else {

                            callback.onError(
                                    "Delete failed: HTTP "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        callback.onError(
                                "Network error: "
                                        + t.getMessage()
                        );
                    }
                });
    }

    // ---------------------------------------------------------
    // LOCAL DELETE (used for tests)
    // ---------------------------------------------------------
    public boolean deleteVehicle(Vehicle vehicle) {

        db.vehicleDao().deleteVehicle(vehicle);

        return true;
    }

    // ---------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------
    public interface VehicleCallback {
        void onSuccess(List<Vehicle> vehicles);
        void onError(String error);
    }

    public interface AddVehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError(String error);
    }

    public interface SingleVehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError(String error);
    }

    public interface DeleteVehicleCallback {
        void onSuccess();
        void onError(String error);
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------
    private String validateVehicle(Vehicle vehicle) {

        if (vehicle.getMake().isEmpty()
                || vehicle.getModel().isEmpty()
                || vehicle.getLocation().isEmpty()) {

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
                                sdf.parse(vehicle.getStartDate())
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