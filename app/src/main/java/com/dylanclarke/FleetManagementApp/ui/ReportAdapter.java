package com.dylanclarke.FleetManagementApp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.Vehicle;

import java.util.List;

/**
 * RecyclerView adapter responsible for displaying
 * maintenance report rows.
 */
public class ReportAdapter
        extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private static final String UNKNOWN_VEHICLE =
            "Unknown";

    private static final String EMPTY_LOCATION =
            "-";

    private static final String ALERT_ENABLED =
            "Yes";

    private static final String ALERT_DISABLED =
            "No";

    private final List<MaintenanceRecord> records;

    private final List<Vehicle> vehicles;

    public ReportAdapter(
            List<MaintenanceRecord> records,
            List<Vehicle> vehicles
    ) {

        this.records = records;
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_report_row,
                                parent,
                                false
                        );

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReportViewHolder holder,
            int position
    ) {

        if (records == null
                || position >= records.size()) {

            return;
        }

        MaintenanceRecord record =
                records.get(position);

        Vehicle vehicle =
                findVehicleForRecord(record);

        bindVehicleData(holder, vehicle);

        bindMaintenanceData(holder, record);
    }

    @Override
    public int getItemCount() {

        return records != null
                ? records.size()
                : 0;
    }

    /**
     * Finds the associated vehicle for a maintenance record.
     */
    private Vehicle findVehicleForRecord(
            MaintenanceRecord record
    ) {

        if (vehicles == null) {
            return null;
        }

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getId() != null
                    && vehicle.getId() == record.getVehicleId()) {

                return vehicle;
            }
        }

        return null;
    }

    /**
     * Binds vehicle-related data to the row.
     */
    private void bindVehicleData(
            ReportViewHolder holder,
            Vehicle vehicle
    ) {

        if (vehicle != null) {

            holder.tvVehicle.setText(
                    vehicle.getMake()
                            + " "
                            + vehicle.getModel()
            );

            holder.tvLocation.setText(
                    vehicle.getLocation()
            );

        } else {

            holder.tvVehicle.setText(
                    UNKNOWN_VEHICLE
            );

            holder.tvLocation.setText(
                    EMPTY_LOCATION
            );
        }
    }

    /**
     * Binds maintenance-related data to the row.
     */
    private void bindMaintenanceData(
            ReportViewHolder holder,
            MaintenanceRecord record
    ) {

        holder.tvServiceDate.setText(
                record.getServiceDate()
        );

        holder.tvDescription.setText(
                record.getDescription()
        );

        holder.tvAlert.setText(
                record.isAlertsEnabled()
                        ? ALERT_ENABLED
                        : ALERT_DISABLED
        );
    }

    /**
     * ViewHolder for report row views.
     */
    static class ReportViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView tvVehicle;

        private final TextView tvLocation;

        private final TextView tvServiceDate;

        private final TextView tvDescription;

        private final TextView tvAlert;

        public ReportViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvVehicle =
                    itemView.findViewById(R.id.tvVehicle);

            tvLocation =
                    itemView.findViewById(R.id.tvLocation);

            tvServiceDate =
                    itemView.findViewById(R.id.tvServiceDate);

            tvDescription =
                    itemView.findViewById(R.id.tvDescription);

            tvAlert =
                    itemView.findViewById(R.id.tvAlert);
        }
    }
}