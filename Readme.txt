Fleet Management
Purpose

Fleet Management is an Android mobile application designed to help users manage a fleet of vehicles. The app provides functionality to:

Add, edit, and delete vehicles.

Track maintenance records for each vehicle.

Set optional alerts for maintenance schedules.

View detailed information about vehicles and their maintenance history.

The app uses a Room database for local storage and includes validation to ensure data integrity.

How to Operate the Application
Launch the App

Open the app on your Android device.

The Home Screen displays buttons to view all vehicles.

View Vehicles

Tap “VIEW VEHICLES” to see a list of all saved vehicles.

Vehicles can be searched using the search functionality.

Add/Edit a Vehicle

Tap the Add button to create a new vehicle.

Enter vehicle details:

Title (optional descriptive name)

Make & Model

Year

Location

Maintenance alert preferences

Start and end dates

Save to add the vehicle to the list.

Tap a vehicle to open its detail view.

Edit or delete vehicles using the available options.

View Maintenance Records

In a vehicle’s detail view, tap “View Maintenance” to see all associated records.

Add new records with:

Title

Description

Dates

Optional alerts

Validation ensures maintenance dates are within the vehicle’s active date range.

Alerts & Validation

Alerts are handled via Android’s alarm service.

Validation ensures:

Required fields are filled.

Years are reasonable.

Start and end dates are valid.

Maintenance records fall within the parent vehicle dates.

Testing
Unit Tests

The app includes JUnit tests for core functionality:

Add, edit, delete vehicles

Search functionality

Data validation

Tests are located in:
app/src/androidTest/java/com/example/d308project/VehicleRepositoryTest.kt

Screenshots of test results and validations were captured during development.

APK Deployment
Tested Environment

Android SDK Version: 35

Minimum SDK Version: 26 (Android 8.0 - Oreo)

Compatible with most devices running Android 8.0 or later.

Installation

Download the signed APK (FleetManagement-1.0.apk) from the GitHub Pages link.

Enable installation from unknown sources if needed.

Open the APK on your device to install.

Git Repository

https://gitlab.com/wgu-gitlab-environment/student-repos/dcla500/d424-software-engineering-capstone/-/tree/capstone?ref_type=heads