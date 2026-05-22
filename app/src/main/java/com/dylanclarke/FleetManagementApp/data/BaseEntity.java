package com.dylanclarke.FleetManagementApp.data;

import androidx.annotation.NonNull;

/**
 * Base model for application entities that share a common ID
 * and user-facing display name.
 */
public abstract class BaseEntity {

    // Primary identifier from backend API/database
    protected Long id;

    /**
     * Returns the entity ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the entity ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns a user-friendly display name for UI rendering.
     */
    @NonNull
    public abstract String displayName();
}