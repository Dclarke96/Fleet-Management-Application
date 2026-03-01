package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.dylanclarke.FleetManagementApp.R;
import androidx.appcompat.app.AppCompatActivity;


// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class MainActivity extends AppCompatActivity {

    Button btnViewVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewVehicles = findViewById(R.id.btnGoToVehicles); // updated ID
        btnViewVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VehicleListActivity.class);
                startActivity(intent);
            }
        });
    }
}

