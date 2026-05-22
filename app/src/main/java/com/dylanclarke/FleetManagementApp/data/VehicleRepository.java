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

/**
 * Repository responsible for vehicle-related API operations.
 */
public class VehicleRepository {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final ApiService apiService;

    public VehicleRepository(Context context) {

        apiService = ApiClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ---------------------------------------------------------
    // GET ALL VEHICLES
    // ---------------------------------------------------------

    public void getAllVehicles(
            VehicleCallback callback
    ) {

        apiService.getVehicles()
                .enqueue(new Callback<ApiResponse<PageResponse<Vehicle>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<Vehicle>>> call,
                            Response<ApiResponse<PageResponse<Vehicle>>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null
                                && response.body().getData().content != null) {

                            callback.onSuccess(
                                    response.body().getData().content
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

                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // GET VEHICLE BY ID
    // ---------------------------------------------------------

    public void getVehicleById(
            Long vehicleId,
            SingleVehicleCallback callback
    ) {

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
                                    "Failed to load vehicle (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Vehicle>> call,
                            Throwable t
                    ) {

                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // ADD VEHICLE
    // ---------------------------------------------------------

    public void addVehicle(
            Vehicle vehicle,
            AddVehicleCallback callback
    ) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        VehicleRequest request = buildVehicleRequest(vehicle);

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

                            callback.onError(
                                    buildServerError(response)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Vehicle>> call,
                            Throwable t
                    ) {

                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // UPDATE VEHICLE
    // ---------------------------------------------------------

    public void updateVehicle(
            Vehicle vehicle,
            UpdateVehicleCallback callback
    ) {

        String validationError = validateVehicle(vehicle);

        if (validationError != null) {
            callback.onError(validationError);
            return;
        }

        if (vehicle.getId() == null) {
            callback.onError("Vehicle ID is null");
            return;
        }

        VehicleRequest request = buildVehicleRequest(vehicle);

        apiService.updateVehicle(vehicle.getId(), request)
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
                                    "Update failed (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Vehicle>> call,
                            Throwable t
                    ) {

                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // DELETE VEHICLE
    // ---------------------------------------------------------

    public void deleteVehicle(
            long vehicleId,
            DeleteVehicleCallback callback
    ) {

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
                                    "Delete failed (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    /**
     * Maps a Vehicle model into an API request object.
     */
    private VehicleRequest buildVehicleRequest(
            Vehicle vehicle
    ) {

        VehicleRequest request = new VehicleRequest();

        request.title = vehicle.getTitle();
        request.make = vehicle.getMake();
        request.model = vehicle.getModel();
        request.vehicleYear = vehicle.getYear();
        request.location = vehicle.getLocation();
        request.maintenanceAlertsEnabled =
                vehicle.isMaintenanceAlertsEnabled();
        request.startDate = vehicle.getStartDate();
        request.endDate = vehicle.getEndDate();

        return request;
    }

    /**
     * Builds a standardized network error message.
     */
    private String buildNetworkError(Throwable t) {
        return "Network error: " + t.getMessage();
    }

    /**
     * Extracts server error details from API responses.
     */
    private String buildServerError(Response<?> response) {

        String errorBody = "";

        try {

            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }

        } catch (Exception ignored) {
        }

        return "Server error: HTTP "
                + response.code()
                + " "
                + errorBody;
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    /**
     * Validates vehicle data before API submission.
     */
    private String validateVehicle(
            Vehicle vehicle
    ) {

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

        SimpleDateFormat sdf =
                new SimpleDateFormat(DATE_FORMAT, Locale.US);

        sdf.setLenient(false);

        try {

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

            return "Invalid date format (" + DATE_FORMAT + ")";
        }

        return null;
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

    public interface UpdateVehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError(String error);
    }

    public interface DeleteVehicleCallback {
        void onSuccess();
        void onError(String error);
    }
}