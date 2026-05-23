# 🏗 Fleet Management App — Architecture Overview

## 1. System Overview

The Fleet Management Android application is an API-driven mobile client that communicates with a Spring Boot backend to manage vehicle and maintenance data.

The system is designed as a **client-server architecture**, where:

- The Android application handles user interaction and presentation logic
- The backend REST API handles business logic, authentication, and data persistence

---

## 2. High-Level Architecture

The application follows a **layered architecture using the Repository pattern**.
UI Layer (Activities)
↓
Repository Layer (Data Access & Business Logic)
↓
Networking Layer (ApiClient + Retrofit ApiService)
↓
REST API (Spring Boot Backend)


---

## 3. Layer Responsibilities

### 3.1 UI Layer (Activities)

The UI layer is responsible for:
- Rendering screens
- Handling user input
- Navigating between screens
- Displaying data received from the repository layer

Activities directly interact with repository classes to request or modify data.

---

### 3.2 Repository Layer

The repository layer acts as the central data abstraction layer.

Responsibilities include:
- Managing API requests
- Processing API responses
- Converting request/response objects
- Providing clean data to the UI layer

This layer ensures the UI does not directly depend on networking logic.

---

### 3.3 Networking Layer

The networking layer is built using:
- Retrofit (HTTP client abstraction)
- OkHttp (low-level HTTP client)
- ApiClient (central configuration manager)

#### ApiClient Responsibilities:
- Initialize Retrofit instance
- Define base URL: http://10.0.2.2:8080/
- Configure HTTP logging
- Attach JWT authentication interceptor
- Manage network timeouts

---

### 3.4 Backend REST API

The backend is a Spring Boot application responsible for:
- Vehicle management
- Maintenance tracking
- Reporting
- Authentication and authorization

All persistent data is managed on the server side.

---

## 4. Authentication Flow

The application uses **JWT-based authentication**.

### Flow:

1. User logs in via login screen
2. Credentials are sent to backend API
3. Backend returns a JWT token
4. Token is stored locally in `SharedPreferences`
5. All future API requests include the token via: Authorization: Bearer <token>
6. Backend validates token for secured endpoints

Token injection is handled automatically via an OkHttp interceptor inside `ApiClient`.

---

## 5. Data Flow Example (Vehicle List)

Example of how data flows through the system:

1. User opens Vehicle List screen
2. `VehicleListActivity` requests data from `VehicleRepository`
3. Repository calls Retrofit `ApiService`
4. ApiClient attaches JWT token automatically
5. Backend returns vehicle data
6. Repository passes data back to Activity
7. UI updates list view

---

## 6. Core Modules

### Vehicle Management
- Create, edit, delete vehicles
- View detailed vehicle information

### Maintenance Management
- Add maintenance records per vehicle
- Edit and delete maintenance entries
- View maintenance history

### Reporting
- Generate summary reports of fleet and maintenance activity

---

## 7. Design Philosophy

This application was originally built as a **school project**, then refactored into a **portfolio-grade system**, and is currently being evolved toward a **product-style architecture**.

The focus of the current architecture is:

- Clear separation of concerns
- Maintainable layered structure
- API-driven data flow
- Scalable backend integration
- Incremental path toward more advanced architecture patterns (e.g., MVVM, offline caching)

---

## 8. Future Architectural Improvements

Planned or potential enhancements:

- Introduce ViewModel layer for MVVM migration
- Add reactive state handling (LiveData or Flow)
- Implement offline caching (Room database)
- Improve error handling and retry mechanisms
- Add API response caching layer
- Improve onboarding and first-time user experience flow
