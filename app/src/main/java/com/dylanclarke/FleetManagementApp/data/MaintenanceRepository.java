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

    private final AppDatabase db;
    private final ApiService apiService;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public MaintenanceRepository(Context context) {

        db = AppDatabase.getInstance(context);

        apiService = ApiClient
                .getClient(context)
                .create(ApiService.class);
    }

    // ---------------------------------------------------------
    // GET maintenance for vehicle (API - PAGED)
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

                            callback.onSuccess(
                                    response.body()
                                            .getData()
                                            .content
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

                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // GET ALL maintenance (API - PAGED SAFE CONVERSION)
    // ---------------------------------------------------------
    public void getAllMaintenance(MaintenanceCallback callback) {

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
    // ADD maintenance
    // ---------------------------------------------------------
    public void addMaintenance(
            MaintenanceRecord record,
            AddMaintenanceCallback callback
    ) {

        MaintenanceRequest request = new MaintenanceRequest();

        request.vehicleId = (long) record.getVehicleId();
        request.description = record.getDescription();
        request.date = record.getServiceDate();

        // backend requires cost (temporary default)
        request.cost = 0.0;

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

                            callback.onError(
                                    "Add failed: HTTP " + response.code()
                            );
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
    // UPDATE maintenance
    // ---------------------------------------------------------
    public void updateMaintenance(
            MaintenanceRecord record,
            UpdateMaintenanceCallback callback
    ) {

        MaintenanceRequest request = new MaintenanceRequest();
        request.vehicleId = (long) record.getVehicleId();
        request.description = record.getDescription();
        request.date = record.getServiceDate();
        request.cost = 0.0;
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
    // DELETE maintenance
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
    // LOCAL (TEMP ONLY)
    // ---------------------------------------------------------
    public MaintenanceRecord getMaintenanceByIdLocal(int id) {
        return db.maintenanceDao().getMaintenanceById(id);
    }

    public void deleteMaintenanceLocal(MaintenanceRecord record) {
        db.maintenanceDao().deleteMaintenance(record);
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------
    private String validateRecord(MaintenanceRecord record) {

        if (record.getDescription() == null || record.getDescription().trim().isEmpty()
                || record.getServiceDate() == null || record.getServiceDate().trim().isEmpty()) {
            return "Description and service date are required";
        }

        try {
            sdf.setLenient(false);
            sdf.parse(record.getServiceDate());
        } catch (ParseException e) {
            return "Invalid date format (yyyy-MM-dd)";
        }

        return null;
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