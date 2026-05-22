package com.dylanclarke.FleetManagementApp.network;

/**
 * Request body for user authentication (login endpoint).
 */
public class LoginRequest {

    private final String username;
    private final String password;

    /**
     * Creates a login request with user credentials.
     *
     * @param username user login identifier
     * @param password user authentication password
     */
    public LoginRequest(
            String username,
            String password
    ) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username for authentication.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password for authentication.
     */
    public String getPassword() {
        return password;
    }
}