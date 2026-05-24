package com.dylanclarke.FleetManagementApp.util;

import com.dylanclarke.FleetManagementApp.data.Vehicle;

public class VehicleValidator {

    public static class ValidationResult {
        public boolean isValid;
        public String message;

        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }

    public static ValidationResult validate(Vehicle vehicle) {

        if (vehicle == null) {
            return ValidationResult.error("Vehicle cannot be null");
        }

        if (isBlank(vehicle.getTitle())) {
            return ValidationResult.error("Title cannot be blank");
        }

        if (isBlank(vehicle.getMake())) {
            return ValidationResult.error("Make cannot be blank");
        }

        if (isBlank(vehicle.getModel())) {
            return ValidationResult.error("Model cannot be blank");
        }

        if (isBlank(vehicle.getLocation())) {
            return ValidationResult.error("Location cannot be blank");
        }

        if (vehicle.getYear() == 0) {
            return ValidationResult.error("Year cannot be null");
        }

        if (vehicle.getYear() < 1886 || vehicle.getYear() > 3000) {
            return ValidationResult.error("Year is out of valid range");
        }

        return ValidationResult.ok();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}