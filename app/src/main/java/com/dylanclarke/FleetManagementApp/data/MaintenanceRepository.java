package com.dylanclarke.FleetManagementApp.data;

import android.content.Context;

import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.MaintenanceRequest;

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
    // API: GET maintenance for vehicle
    // ---------------------------------------------------------
    public void getMaintenanceForVehicle(long vehicleId, MaintenanceCallback callback) {

        apiService.getMaintenanceForVehicle(vehicleId)
                .enqueue(new Callback<ApiResponse<List<MaintenanceRecord>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<MaintenanceRecord>>> call,
                            Response<ApiResponse<List<MaintenanceRecord>>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {

                            callback.onError("Failed to load maintenance (HTTP " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<MaintenanceRecord>>> call,
                            Throwable t
                    ) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // API: Get all maintenance
    // ---------------------------------------------------------
    public void getAllMaintenance(MaintenanceCallback callback) {

        apiService.getMaintenance()
                .enqueue(new Callback<ApiResponse<List<MaintenanceRecord>>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<List<MaintenanceRecord>>> call,
                                           Response<ApiResponse<List<MaintenanceRecord>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Failed to load maintenance");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<MaintenanceRecord>>> call,
                                          Throwable t) {

                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // LOCAL GET (Room fallback - still used for UI loading)
    // ---------------------------------------------------------
    public MaintenanceRecord getMaintenanceById(int id) {
        return db.maintenanceDao().getMaintenanceById(id);
    }

    // ---------------------------------------------------------
    // API: ADD maintenance
    // ---------------------------------------------------------
    public void addMaintenance(MaintenanceRecord record,
                               AddMaintenanceCallback callback) {

        MaintenanceRequest request = new MaintenanceRequest();
        request.vehicleId = record.getVehicleId();
        request.description = record.getDescription();
        request.serviceDate = record.getServiceDate();
        request.alertsEnabled = record.isAlertsEnabled();

        apiService.addMaintenance(request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<MaintenanceRecord>> call,
                                           Response<ApiResponse<MaintenanceRecord>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Add failed: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MaintenanceRecord>> call,
                                          Throwable t) {

                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // API: UPDATE maintenance
    // ---------------------------------------------------------
    public void updateMaintenance(MaintenanceRecord record,
                                  UpdateMaintenanceCallback callback) {

        MaintenanceRequest request = new MaintenanceRequest();
        request.vehicleId = record.getVehicleId();
        request.description = record.getDescription();
        request.serviceDate = record.getServiceDate();
        request.alertsEnabled = record.isAlertsEnabled();

        apiService.updateMaintenance(record.getId(), request)
                .enqueue(new Callback<ApiResponse<MaintenanceRecord>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<MaintenanceRecord>> call,
                                           Response<ApiResponse<MaintenanceRecord>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError("Update failed: HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MaintenanceRecord>> call,
                                          Throwable t) {

                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // API: DELETE maintenance
    // ---------------------------------------------------------
    public void deleteMaintenance(long id, DeleteMaintenanceCallback callback) {

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
    // LOCAL (TEMP - keep for now)
    // ---------------------------------------------------------
    public List<MaintenanceRecord> getMaintenanceForVehicleLocal(int vehicleId) {
        return db.maintenanceDao().getMaintenanceForVehicle(vehicleId);
    }

    public void deleteMaintenanceLocal(MaintenanceRecord record) {
        db.maintenanceDao().deleteMaintenance(record);
    }

    // ---------------------------------------------------------
    // VALIDATION (kept simple for now)
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