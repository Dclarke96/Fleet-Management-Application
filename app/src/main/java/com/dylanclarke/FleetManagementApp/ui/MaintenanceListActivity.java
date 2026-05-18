package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRepository;

import java.util.List;

public class MaintenanceListActivity extends AppCompatActivity {

    private MaintenanceRepository maintenanceRepo;
    private long vehicleId;
    private LinearLayout maintenanceContainer;
    private Button btnAddMaintenance, btnBack;

    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_list);

        maintenanceContainer = findViewById(R.id.maintenanceContainer);
        btnAddMaintenance = findViewById(R.id.btnAddMaintenance);
        btnBack = findViewById(R.id.backButton);

        maintenanceRepo = new MaintenanceRepository(getApplicationContext());

        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getLongExtra("vehicleId", -1L);
        }

        btnAddMaintenance.setOnClickListener(v -> {
            Intent intent = new Intent(this, MaintenanceDetailActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (vehicleId != -1L) {
            loadMaintenanceRecords();
        }
    }

    private void loadMaintenanceRecords() {

        if (isLoading) return;
        isLoading = true;

        maintenanceContainer.removeAllViews();

        maintenanceRepo.getMaintenanceForVehicle(
                vehicleId,
                new MaintenanceRepository.MaintenanceCallback() {

                    @Override
                    public void onSuccess(List<MaintenanceRecord> records) {

                        runOnUiThread(() -> {

                            maintenanceContainer.removeAllViews();

                            for (MaintenanceRecord record : records) {

                                View item = getLayoutInflater()
                                        .inflate(R.layout.item_maintenance_record, null);

                                TextView txtDescription =
                                        item.findViewById(R.id.txtMaintenanceDescription);

                                TextView txtDate =
                                        item.findViewById(R.id.txtMaintenanceDate);

                                Button btnEdit =
                                        item.findViewById(R.id.btnEditMaintenance);

                                Button btnDelete =
                                        item.findViewById(R.id.btnDeleteMaintenance);

                                txtDescription.setText(record.getDescription());
                                txtDate.setText(record.getServiceDate());

                                btnEdit.setOnClickListener(v -> {
                                    Intent intent = new Intent(
                                            MaintenanceListActivity.this,
                                            MaintenanceDetailActivity.class
                                    );

                                    intent.putExtra("maintenanceId", record.getId());
                                    intent.putExtra("vehicleId", vehicleId);

                                    startActivity(intent);
                                });

                                btnDelete.setOnClickListener(v -> {

                                    maintenanceRepo.deleteMaintenance(
                                            record.getId(),
                                            new MaintenanceRepository.DeleteMaintenanceCallback() {

                                                @Override
                                                public void onSuccess() {

                                                    runOnUiThread(() -> {
                                                        Toast.makeText(
                                                                MaintenanceListActivity.this,
                                                                "Deleted",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        loadMaintenanceRecords();
                                                    });
                                                }

                                                @Override
                                                public void onError(String error) {

                                                    runOnUiThread(() ->
                                                            Toast.makeText(
                                                                    MaintenanceListActivity.this,
                                                                    error,
                                                                    Toast.LENGTH_LONG
                                                            ).show()
                                                    );
                                                }
                                            }
                                    );
                                });

                                maintenanceContainer.addView(item);
                            }

                            isLoading = false;
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() -> {
                            isLoading = false;

                            Toast.makeText(
                                    MaintenanceListActivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }
}