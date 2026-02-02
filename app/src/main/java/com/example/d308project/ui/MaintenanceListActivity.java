package com.example.d308project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.MaintenanceRecord;

import java.util.List;

public class MaintenanceListActivity extends AppCompatActivity {

    private AppDatabase db;
    private int vacationId;
    private LinearLayout excursionContainer;
    private Button btnAddExcursion, btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        excursionContainer = findViewById(R.id.excursionContainer);
        btnAddExcursion = findViewById(R.id.btnAddExcursion);
        btnBack = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
            if (vacationId != -1) {
                loadExcursions();
            }
        }

        btnAddExcursion.setOnClickListener(v -> {
            Intent intent = new Intent(this, MaintenanceDetailActivity.class);
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions();
    }

    private void loadExcursions() {
        excursionContainer.removeAllViews();

        List<MaintenanceRecord> maintenanceRecords = db.excursionDao().getExcursionsForVacation(vacationId);
        for (MaintenanceRecord maintenanceRecord : maintenanceRecords) {
            View item = getLayoutInflater().inflate(R.layout.item_excursion, null);

            TextView txtTitle = item.findViewById(R.id.txtExcursionTitle);
            TextView txtDate = item.findViewById(R.id.txtExcursionDate);
            Button btnEdit = item.findViewById(R.id.btnEditExcursion);
            Button btnDelete = item.findViewById(R.id.btnDeleteExcursion);

            txtTitle.setText(maintenanceRecord.title);
            txtDate.setText(maintenanceRecord.date);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, MaintenanceDetailActivity.class);
                intent.putExtra("excursionId", maintenanceRecord.id);
                intent.putExtra("vacationId", vacationId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                db.excursionDao().deleteExcursionById(maintenanceRecord.id);
                loadExcursions();
            });

            excursionContainer.addView(item);
        }
    }
}
