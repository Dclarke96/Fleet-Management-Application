package com.example.d308project.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "vacations")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "hotel")
    public String hotel;

    @ColumnInfo(name = "start_date")
    public String startDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

    @ColumnInfo(name = "alerts_enabled")
    public boolean alertsEnabled;

    public Vacation() {
    }

    public Vacation(String title, String hotel, String startDate, String endDate, boolean alertsEnabled) {
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertsEnabled = alertsEnabled;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " (" + startDate + " to " + endDate + ")";
    }
}
