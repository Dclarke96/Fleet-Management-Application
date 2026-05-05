package com.dylanclarke.FleetManagementApp.data;

import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Base class for all database entities.
 * Demonstrates inheritance and encapsulation.
 */
public abstract class BaseEntity {

    @PrimaryKey(autoGenerate = true)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public abstract String displayName();
}
