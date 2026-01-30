package com.example.d308project.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Vehicle.class,
                parentColumns = "id",
                childColumns = "vacationOwnerId",
                onDelete = ForeignKey.RESTRICT
        )
)
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String title = "";

    @NonNull
    public String date = "";

    @ColumnInfo(name = "alerts_enabled")
    public boolean alertsEnabled;

    public int vacationOwnerId;
}
