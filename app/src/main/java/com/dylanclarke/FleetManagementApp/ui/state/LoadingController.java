package com.dylanclarke.FleetManagementApp.ui.state;

import android.view.View;
import android.widget.Button;

import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * Handles loading state for screens.
 * Disables buttons, blocks UI, and shows loading spinner overlay.
 */
public class LoadingController {

    private final Button primaryButton;
    private final View overlay;
    private final View[] disableDuringLoading;
    private final CircularProgressIndicator progressIndicator;

    public LoadingController(
            Button primaryButton,
            CircularProgressIndicator progressIndicator,
            View overlay,
            View... disableDuringLoading
    ) {
        this.primaryButton = primaryButton;
        this.progressIndicator = progressIndicator;
        this.overlay = overlay;
        this.disableDuringLoading = disableDuringLoading;
    }

    public void show() {

        if (primaryButton != null) {
            primaryButton.setEnabled(false);
            primaryButton.setText("Loading...");
        }

        for (View v : disableDuringLoading) {
            if (v != null) v.setEnabled(false);
        }

        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {

        if (primaryButton != null) {
            primaryButton.setEnabled(true);
            primaryButton.setText("Save Vehicle");
        }

        for (View v : disableDuringLoading) {
            if (v != null) v.setEnabled(true);
        }

        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.GONE);
        }
    }
}