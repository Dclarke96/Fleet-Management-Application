package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.MaintenanceRequest;
import com.dylanclarke.FleetManagementApp.network.PageResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceRepository {

    private final ApiService apiService;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public MaintenanceRepository(Context context) {

        apiService = ApiClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ---------------------------------------------------------
    // GET BY ID
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

                        if (response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Failed to load maintenance");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // GET maintenance for vehicle
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

                        if (response.body() != null
                                && response.body().getData() != null
                                && response.body().getData().content != null) {

                            callback.onSuccess(response.body().getData().content);

                        } else {
                            callback.onError("Failed to load maintenance (HTTP " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // GET ALL maintenance
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

                        if (response.body() != null
                                && response.body().getData() != null
                                && response.body().getData().content != null) {

                            callback.onSuccess(
                                    response.body()
                                            .getData()
                                            .content
                            );

                        } else {
                            callback.onError("Failed to load maintenance");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MaintenanceRecord>>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
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

        MaintenanceRequest request = new MaintenanceRequest();

        request.vehicleId = (long) record.getVehicleId();
        request.description = record.getDescription();
        request.date = record.getServiceDate();

        // ✅ FIX: use real value instead of forcing 0
        request.cost = record.getCost();

        request.alertsEnabled = record.isAlertsEnabled();

        apiService.addMaintenance(request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Response<ApiResponse<MaintenanceRecord>> response
                    ) {

                        if (response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Add failed: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
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

        MaintenanceRequest request = new MaintenanceRequest();

        request.vehicleId = (long) record.getVehicleId();
        request.description = record.getDescription();
        request.date = record.getServiceDate();

        // ✅ FIX: stop overwriting with 0
        request.cost = record.getCost();

        request.alertsEnabled = record.isAlertsEnabled();

        apiService.updateMaintenance(record.getId(), request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Response<ApiResponse<MaintenanceRecord>> response
                    ) {

                        if (response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Update failed: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<MaintenanceRecord>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // DELETE (unchanged)
    // ---------------------------------------------------------
    public void deleteMaintenance(
            long id,
            DeleteMaintenanceCallback callback
    ) {

        apiService.deleteMaintenance(id)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Delete failed: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // CALLBACKS (unchanged)
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