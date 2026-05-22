package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.MaintenanceRequest;
import com.dylanclarke.FleetManagementApp.network.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository responsible for maintenance-related API operations.
 */
public class MaintenanceRepository {

    private final ApiService apiService;

    public MaintenanceRepository(Context context) {
        apiService = ApiClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ---------------------------------------------------------
    // GET SINGLE RECORD
    // ---------------------------------------------------------

    public void getMaintenanceById(
            long maintenanceId,
            SingleMaintenanceCallback callback
    ) {

        apiService.getMaintenanceById(maintenanceId)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Response<ApiResponse<MaintenanceRecord>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError(
                                    "Failed to load maintenance (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // GET VEHICLE MAINTENANCE
    // ---------------------------------------------------------

    public void getMaintenanceForVehicle(
            long vehicleId,
            MaintenanceCallback callback
    ) {

        apiService.getMaintenanceForVehicle(vehicleId)
                .enqueue(new Callback<ApiResponse<PageResponse<MaintenanceRecord>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Response<ApiResponse<PageResponse<MaintenanceRecord>>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null
                                && response.body().getData().getContent() != null) {

                            callback.onSuccess(
                                    response.body().getData().getContent()
                            );

                        } else {
                            callback.onError(
                                    "Failed to load maintenance (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Throwable t
                    ) {
                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // GET ALL MAINTENANCE
    // ---------------------------------------------------------

    public void getAllMaintenance(
            MaintenanceCallback callback
    ) {

        apiService.getMaintenance()
                .enqueue(new Callback<ApiResponse<PageResponse<MaintenanceRecord>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Response<ApiResponse<PageResponse<MaintenanceRecord>>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null
                                && response.body().getData().getContent() != null) {

                            callback.onSuccess(
                                    response.body().getData().getContent()
                            );

                        } else {
                            callback.onError(
                                    "Failed to load maintenance (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Throwable t
                    ) {
                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // ADD
    // ---------------------------------------------------------

    public void addMaintenance(
            MaintenanceRecord record,
            AddMaintenanceCallback callback
    ) {

        MaintenanceRequest request = buildRequest(record);

        apiService.addMaintenance(request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Response<ApiResponse<MaintenanceRecord>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError(
                                    "Add failed (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    public void updateMaintenance(
            MaintenanceRecord record,
            UpdateMaintenanceCallback callback
    ) {

        MaintenanceRequest request = buildRequest(record);

        apiService.updateMaintenance(record.getId(), request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Response<ApiResponse<MaintenanceRecord>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError(
                                    "Update failed (HTTP "
                                            + response.code() + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError(buildNetworkError(t));
                    }
                });
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    public void deleteMaintenance(
            long id,
            DeleteMaintenanceCallback callback
    ) {

        apiService.deleteMaintenance(id)
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
     * Maps a MaintenanceRecord into an API request payload.
     */
    private MaintenanceRequest buildRequest(
            MaintenanceRecord record
    ) {

        return new MaintenanceRequest(
                (long) record.getVehicleId(),
                record.getDescription(),
                record.getServiceDate(),
                record.getCost(),
                record.isAlertsEnabled()
        );
    }

    /**
     * Builds a user-friendly network error message.
     */
    private String buildNetworkError(Throwable t) {
        return "Network error: " + t.getMessage();
    }

    // ---------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------

    public interface MaintenanceCallback {
        void onSuccess(List<MaintenanceRecord> records);
        void onError(String error);
    }

    public interface SingleMaintenanceCallback {
        void onSuccess(MaintenanceRecord record);
        void onError(String error);
    }

    public interface AddMaintenanceCallback {
        void onSuccess(MaintenanceRecord record);
        void onError(String error);
    }

    public interface UpdateMaintenanceCallback {
        void onSuccess(MaintenanceRecord record);
        void onError(String error);
    }

    public interface DeleteMaintenanceCallback {
        void onSuccess();
        void onError(String error);
    }
}