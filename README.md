🚀 Fleet Management Android App (Frontend)
Overview

The Fleet Management Android application is a mobile client for managing vehicle and maintenance data within a fleet management system.

It integrates with a Spring Boot REST API backend and uses JWT-based authentication to securely access protected resources.

The application supports vehicle management, maintenance tracking, reporting, and data sharing. It has been refactored from an earlier local-storage prototype into a fully API-driven Android client.

✨ Features
Secure login using JWT authentication
View all vehicles from backend API
Create, update, and delete vehicles
View detailed vehicle information
Add, edit, and delete maintenance records per vehicle
Generate fleet maintenance reports
Set maintenance alerts
Share vehicle information using system share intent
Search and filter vehicles by multiple attributes
Input validation for data integrity
🧱 Tech Stack
Java (primary) + Kotlin (partial usage)
Android SDK
Spring Boot REST API (backend integration)
Retrofit (network communication)
OkHttp (HTTP client)
Custom ApiClient wrapper
Repository pattern (data abstraction layer)
JWT authentication (Bearer token)
Material Design components
JUnit testing
🏗 Architecture

The application follows a layered architecture using the Repository pattern.

UI Layer (Activities)
↓
Repository Layer (Data Access & Business Logic)
↓
Networking Layer (ApiClient + Retrofit ApiService)
↓
REST API (Spring Boot Backend)
Architecture Overview
UI Layer (Activities)
Handles user interface rendering, navigation, and user interactions. Activities communicate directly with the repository layer.
Repository Layer
Acts as the central data handler for the application. It manages API calls and prepares data for the UI layer.
Networking Layer
Built using Retrofit and OkHttp, configured through a centralized ApiClient. This layer handles all HTTP communication and authentication headers.
REST API Backend
Provides business logic, authentication, and persistent data storage.
🔐 Authentication
Authentication is handled using JWT (JSON Web Token).
Users log in via the backend API.
The JWT token is stored locally using SharedPreferences.
All authenticated requests include the token in the Authorization: Bearer <token> header.
Token injection is handled automatically via an OkHttp interceptor in ApiClient.
🌐 Backend Integration

The application is fully integrated with a Spring Boot REST API backend responsible for:

Vehicle management
Maintenance tracking
Reporting
Authentication and authorization

All application data is retrieved and persisted through API requests. The app does not use a local database as its primary data source.

📡 Networking Layer

Networking is implemented using Retrofit, configured through a centralized ApiClient.

ApiClient Responsibilities:
Initialize and configure Retrofit instance

Define base URL: http://10.0.2.2:8080/


Attach JWT authentication interceptor
Enable HTTP logging for debugging
Configure network timeouts

This ensures a centralized and maintainable networking setup across the application.

📱 Application Screens
Login Screen
Vehicle List Screen
Vehicle Detail Screen
Maintenance List Screen
Maintenance Detail Screen
Report Screen
🧪 Testing
Unit tests for repository logic
Validation tests for vehicle and maintenance workflows
CRUD operation verification
API integration testing (development environment)
⚙️ Setup Instructions
Clone the repository
Open the project in Android Studio
Sync Gradle dependencies
Ensure the backend Spring Boot server is running

Verify the API base URL in ApiClient: http://10.0.2.2:8080/


Run the application on an emulator or physical device
📌 Notes
The backend API must be running for full functionality
Uses 10.0.2.2 for local emulator backend access
Architecture follows a layered Repository-based design (not MVVM)
The application is API-driven and does not rely on a local database for primary data storage
Designed for development and testing environments
🚀 Future Improvements
Introduce ViewModel layer for full MVVM architecture migration
Add offline caching using Room database
Improve error handling and retry mechanisms
Add token refresh support (if implemented on backend)
Improve first-time user onboarding flow
Optimize API response handling and performance