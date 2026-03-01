package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.AppDatabase;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;

import java.util.List;

// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class MaintenanceListActivity extends AppCompatActivity {

    private AppDatabase db;
    private int vehicleId;
    private LinearLayout maintenanceContainer;
    private Button btnAddMaintenance, btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_list); // Ensure this XML exists

        maintenanceContainer = findViewById(R.id.maintenanceContainer);
        btnAddMaintenance = findViewById(R.id.btnAddMaintenance);
        btnBack = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            if (vehicleId != -1) {
                loadMaintenanceRecords();
            }
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
        loadMaintenanceRecords();
    }

    private void loadMaintenanceRecords() {
        maintenanceContainer.removeAllViews();

        List<MaintenanceRecord> maintenanceRecords = db.maintenanceDao().getMaintenanceForVehicle(vehicleId);
        for (MaintenanceRecord record : maintenanceRecords) {
            View item = getLayoutInflater().inflate(R.layout.item_maintenance_record, null); // Ensure XML matches IDs

            TextView txtDescription = item.findViewById(R.id.txtMaintenanceDescription);
            TextView txtDate = item.findViewById(R.id.txtMaintenanceDate);
            Button btnEdit = item.findViewById(R.id.btnEditMaintenance);
            Button btnDelete = item.findViewById(R.id.btnDeleteMaintenance);

            // Display data
            txtDescription.setText(record.getDescription());
            txtDate.setText(record.getServiceDate());

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, MaintenanceDetailActivity.class);
                intent.putExtra("maintenanceId", record.getId());
                intent.putExtra("vehicleId", vehicleId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                db.maintenanceDao().deleteMaintenanceById(record.getId());
                loadMaintenanceRecords();
            });

            maintenanceContainer.addView(item);
        }
    }
}
