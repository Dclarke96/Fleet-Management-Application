package com.dylanclarke.FleetManagementApp.network;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String timestamp;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}