# Fleet Management Application

Fleet Management is an Android application for managing vehicles and maintenance records. The project began as a university application and was later extended to communicate with the Fleet Management REST API, providing centralized data storage and API-backed workflows.

## Project Evolution

This application represents the starting point of the Fleet Management project and evolved alongside the backend systems that support it.

### Original University Project

The application was initially developed as a university project using **Room** for local data persistence. The original implementation focused on vehicle and maintenance management within the Android application.

### Fleet Management API

After completing the initial application, a **Spring Boot REST API** was developed to provide centralized persistence, authentication, authorization, and multi-user access.

### API Integration

The Android application was subsequently updated to communicate with the Fleet Management API using **Retrofit** and **Gson**. The deployed/tested version uses the API for vehicle and maintenance operations rather than relying on the original local-data workflow.

This progression reflects the evolution of the project from a standalone academic application into a client application backed by a dedicated REST API.

---

## 🛠 Features

* Add, edit, and delete vehicles
* Track maintenance records for each vehicle
* Set optional maintenance alerts
* View detailed vehicle and maintenance information
* Search vehicles by relevant vehicle information
* Generate vehicle and maintenance reports
* Validate user input and business rules
* Communicate with the Fleet Management REST API
* Handle network and server-side errors

---

## 📱 Screenshots

![Home Screen](screenshots/home_screen.png)

![Vehicle Detail](screenshots/vehicle_detail.png)

![Maintenance Records](screenshots/maintenance_records.png)

> Screenshots are stored in the `screenshots/` folder.

---

## 📖 User Guide

The full interactive user guide is available in PDF format:

[View the User Guide](user-guides/FleetManagement_UserGuide.pdf)

> The PDF includes a clickable table of contents for easy navigation.

---

## ⚙ How to Operate the Application

### Launch the App

* Open the Fleet Management app on an Android device.
* The home screen provides access to vehicle management and application features.

### Manage Vehicles

* **Add Vehicle**: Tap Add → enter vehicle details → Save
* **Edit Vehicle**: Select a vehicle → modify fields → Save
* **Delete Vehicle**: Select a vehicle → Delete
* Vehicles with associated maintenance records cannot be deleted when restricted by the application's business rules.

### Manage Maintenance

* Select a vehicle → View Maintenance → Add/Edit/Delete entries
* Maintenance dates are validated against applicable vehicle dates.

### Search Vehicles

* Use the search functionality to filter vehicles by supported vehicle information.

### Generate Reports

* Select **Generate Report** from the main screen to view a summary of vehicles and maintenance information.

---

## 🔌 API Integration

The current application communicates with the Fleet Management REST API for backend operations.

* **Networking:** Retrofit
* **Serialization:** Gson
* **Backend:** Spring Boot REST API
* **Authentication:** JWT-based authentication
* **Data:** Vehicle and maintenance records managed through API requests

The API repository is available here:

[Fleet Management API](https://github.com/Dclarke96/Fleet-Management-API)

---

## 🧪 Testing

The application includes automated Android tests covering core vehicle workflows and validation.

* Add, edit, and delete vehicle operations
* Vehicle search functionality
* Data validation

Tests are located under:

`app/src/androidTest/java/com/dylanclarke/FleetManagementApp/VehicleRepositoryTest.kt`

Screenshots of test results are included in the `screenshots/` folder.

---

## 📦 APK Deployment

* **Tested Environment:** Android SDK 35
* **Minimum SDK:** 26 (Android 8.0 / Oreo)

### Installation

1. Download the signed APK from the project deployment page.
2. Enable installation from unknown sources if prompted.
3. Open the APK and select **Install**.
4. Launch the Fleet Management application.

> A downloadable APK will be provided through the project's deployment/portfolio site.

---

## 🧩 Technology Stack

* Java
* Android SDK
* Retrofit
* Gson
* Material Design
* JUnit
* Android testing framework
* REST API integration
* MVVM architecture

> **Historical technology:** The original university implementation used Room for local persistence. The application was later updated to use the Fleet Management REST API.

---

## 🔗 Related Projects

### Fleet Management API

The Spring Boot backend developed to provide centralized persistence, authentication, authorization, and API access for the Fleet Management application.

[View the Fleet Management API](https://github.com/Dclarke96/Fleet-Management-API)

### Spring Boot API Template

The reusable Spring Boot foundation developed from the engineering patterns and practices established during the Fleet Management API project.

[View the Spring Boot API Template](https://github.com/Dclarke96/Spring-Boot-API-Template)
