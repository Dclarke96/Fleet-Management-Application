package com.dylanclarke.FleetManagementApp.data;

import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Base class for all database entities.
 * Demonstrates inheritance and encapsulation.
 */
public abstract class BaseEntity {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public abstract String displayName();
}
