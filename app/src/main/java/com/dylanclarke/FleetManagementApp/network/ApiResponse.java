package com.dylanclarke.FleetManagementApp.network;

/**
 * Generic API response wrapper used by backend endpoints.
 *
 * @param <T> Type of response payload contained in {@code data}
 */
public class ApiResponse<T> {

    // Indicates whether the request completed successfully
    private boolean success;

    // Main response payload
    private T data;

    // Optional server response message
    private String message;

    // Server-generated timestamp
    private String timestamp;

    /**
     * Returns whether the API request was successful.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the response payload.
     */
    public T getData() {
        return data;
    }

    /**
     * Returns an optional server response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the server timestamp associated with the response.
     */
    public String getTimestamp() {
        return timestamp;
    }
}