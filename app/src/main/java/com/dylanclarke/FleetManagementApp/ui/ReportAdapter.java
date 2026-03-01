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


// SCALABILITY:
// RecyclerView adapter supports dynamic data sets,
// enabling future report types or additional fields
// without changing Activity logic.

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<MaintenanceRecord> records;
    private final List<Vehicle> vehicles;

    public ReportAdapter(List<MaintenanceRecord> records, List<Vehicle> vehicles) {
        this.records = records;
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_row, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        if (records == null || position >= records.size()) return;

        MaintenanceRecord record = records.get(position);

        Vehicle vehicle = null;
        if (vehicles != null) {
            for (Vehicle v : vehicles) {
                if (v.getId() == record.getVehicleId()) {
                    vehicle = v;
                    break;
                }
            }
        }

        if (vehicle != null) {
            holder.tvVehicle.setText(vehicle.getMake() + " " + vehicle.getModel());
            holder.tvLocation.setText(vehicle.getLocation());
        } else {
            holder.tvVehicle.setText("Unknown");
            holder.tvLocation.setText("-");
        }

        holder.tvServiceDate.setText(record.getServiceDate());
        holder.tvDescription.setText(record.getDescription());
        holder.tvAlert.setText(record.isAlertsEnabled() ? "Yes" : "No");
    }

    @Override
    public int getItemCount() {
        return records == null ? 0 : records.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvVehicle, tvLocation, tvServiceDate, tvDescription, tvAlert;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvServiceDate = itemView.findViewById(R.id.tvServiceDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAlert = itemView.findViewById(R.id.tvAlert);
        }
    }
}
