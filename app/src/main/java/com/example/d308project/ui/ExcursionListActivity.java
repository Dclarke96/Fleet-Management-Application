package com.example.d308project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Excursion;

import java.util.List;

public class ExcursionListActivity extends AppCompatActivity {

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
            Intent intent = new Intent(this, ExcursionDetailActivity.class);
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

        List<Excursion> excursions = db.excursionDao().getExcursionsForVacation(vacationId);
        for (Excursion excursion : excursions) {
            View item = getLayoutInflater().inflate(R.layout.item_excursion, null);

            TextView txtTitle = item.findViewById(R.id.txtExcursionTitle);
            TextView txtDate = item.findViewById(R.id.txtExcursionDate);
            Button btnEdit = item.findViewById(R.id.btnEditExcursion);
            Button btnDelete = item.findViewById(R.id.btnDeleteExcursion);

            txtTitle.setText(excursion.title);
            txtDate.setText(excursion.date);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExcursionDetailActivity.class);
                intent.putExtra("excursionId", excursion.id);
                intent.putExtra("vacationId", vacationId);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                db.excursionDao().deleteExcursionById(excursion.id);
                loadExcursions();
            });

            excursionContainer.addView(item);
        }
    }
}
