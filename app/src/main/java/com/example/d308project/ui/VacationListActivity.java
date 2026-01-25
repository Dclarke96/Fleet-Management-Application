package com.example.d308project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Vacation;
import java.util.List;

public class VacationListActivity extends AppCompatActivity {

    private ListView vacationListView;
    private AppDatabase db;

    private List<Vacation> vacations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        db = AppDatabase.getInstance(getApplicationContext());

        vacationListView = findViewById(R.id.vacation_list_view);

        Button btnAddVacation = findViewById(R.id.btnAddVacation);
        btnAddVacation.setOnClickListener(view -> startActivity(new Intent(this, VacationDetailActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVacations();
    }

    private void loadVacations() {
        vacations = db.vacationDao().getAllVacations();

        ArrayAdapter<Vacation> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                vacations
        );

        vacationListView.setAdapter(adapter);

        vacationListView.setOnItemClickListener((parent, view, position, id) -> {
            Vacation selectedVacation = vacations.get(position);
            Intent intent = new Intent(this, VacationDetailActivity.class);
            intent.putExtra("vacationId", selectedVacation.id);
            startActivity(intent);
        });

        vacationListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Vacation selectedVacation = vacations.get(position);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Vacation")
                    .setMessage("Are you sure you want to delete this vacation?")
                    .setPositiveButton("Delete", (dialog, which) -> attemptDeleteVacation(selectedVacation))
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
    }

    private void attemptDeleteVacation(Vacation vacation) {
        int excursionCount = db.excursionDao().countExcursionsForVacation(vacation.id);

        if (excursionCount > 0) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Vacation")
                    .setMessage("This vacation has excursions. Delete them first.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            db.vacationDao().deleteVacation(vacation);
            loadVacations();
            android.widget.Toast.makeText(this, "Vacation deleted", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

}
